const mysql = require('mysql');
const express = require('express');
const bodyParser = require('body-parser');
const axios= require('axios');
const app = express();
const port = 3000;
const config=require('./config.json');

app.set("port", port);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

const OPENAI_API_URL= 'https://api.openai.com/v1/chat/completions'
const OPENAI_API_KEY=config.openai_api_key;


app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.listen(port, () => {
    console.log('Server Runnning...');
});

const connection = mysql.createConnection({
    host: "calendy-database.csiusnffabei.ap-northeast-2.rds.amazonaws.com",
    user: "admin",
    database: "calendy_databse",
    password: "jajuboja10",
    port: 3306
});


const systemPrompt = `Your job is to convert user commands into sql queries in sqlite and return single string. Make the query as short as possible. Do not specify and default values when inserting. Do NOT include any other text.

In the database, three tables exist:
SCHEDULE(id INTEGER, title TEXT, start_time TEXT, end_time TEXT, memo TEXT DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL)

TODO(id INTEGER, title TEXT, due_time TEXT, complete INTEGER DEFAULT 0, memo TEXT DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL)

CATEGORY(id INTEGER, title TEXT, priority INTEGER)

SCHEDULE is an event that has both start time & end time. TODO is a task that only has due time. The term PLAN includes both TODO and SCHEDULE. In PLAN tables, category_id is a foreign key referencing CATEGORY.id. priority is from 1~5, higher the more important. In TODO, complete is a boolean value that represents whether TODO is complete or not.

You will be given (id, title) tuples of existing records in each table, and current time.

Follow [YYYY-MM-DD HH:MM:SS] format for time. When modifying time attributes partially, for example only changing the date, use substr() and concatenation. '오전 12시' means 00:00:00. 'Week' referes to time span from Monday to Sunday. 

-------------------------------------------------------------------------
#Conversion Instructions

First, determine the type of the query as one of the four types given: INSERT/DELETE/UPDATE/SELECT
NEVER consider the type as UPDATE unless when explicitly commanded to do so such as "바꿔, 수정".  Otherwise, INSERT a new plan.

After the query type has been determined, determine whether the PLAN type is SCHEDULE or TODO. Specifying only single date means PLAN type is TODO, and due_time is 23:59:59 of that day.  

When the type is INSERT, find the matching category id from the data given and use the id. If you can't find matching CATEGORY.id from data below, leave it as null. DO NOT use WHERE condition on title of CATEGORY.

When the type is SELECT, always select all attributes using *. Don't use UNION/INTERSECT/EXCEPT. Asking for '브리핑', '요약' means SELECT.

For a command including conditional clause on category, search for the id of the category with matching title in CATEGORY and use the id in query. 
ONLY when there is no matching CATEGORY record, it is a condition on title. NEVER use WHERE condition on title of TODO or SCHEDULE. Instead, search for the id of records with matching title and use id explicitly in query. If there is no match in both SCHEDULE and TODO, output "NO_SUCH_PLAN;" in place of that query. Even when you find the matching id, NEVER forget to include WHERE conditions on time.
`
    
const assistant_exemplars=[
    {
        "role": "user",
        "content": `Current Time: [2023-11-23 20:25:39]

        CATEGORY(id, title): (1, "과제"), (2, "잡일"), (3, "운동"), (4, "약속"), (5, "시험"), (6, "봉사")
        
        TODO(id, title): (1, "쓰레기 버리기"), (2, "밥먹기"), (3, "키보드 사기"), (4, "닭가슴살 시키기"), 
        (5, "엄마한테 전화하기"), (6, "알고리즘 과제2"), (7, "알고리즘 과제1"), 
        (8, "인공지능 과제"), (9, "소개원실 회의"), (10, "회의"), (11, "컴퓨터 구조 과제")
        
        SCHEDULE(id, title): (1, "신영이랑 밥먹기"), (2, "알고리즘 수업"), (3, "헬스'), (4, "과외"),
        (5, "데이트"), (6, "자전거 타기'), (7, "디자인 워크샵"), (8, "영화감상회"),
        (9, "신영이랑 커피"), (10, "고급 수학 과외")`
    },
    {
        "role": "user",
        "content": "23년 11월 18일 컴퓨터구조 과제 삭제해줘"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE id=11 AND date(due_time)='2023-11-18';"`
    },
    {
        "role": "user",
        "content": "내일 과제 모두 삭제"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE category_id=1 AND date(due_time)='2023-11-24';”`
    },
    {
        "role": "user",
        "content": "내일 8시 알골 과제 추가"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time, category_id) VALUES ('알골 과제', '2023-11-24 08:00:00', 1);"`
    },
    {
        "role": "user",
        "content": "이번 달 매주 화요일에 헬스하기 추가해줘"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO(title, due_time) VALUES('헬스하기', '2023-11-07 23:59:59'), ('헬스하기', '2023-11-14 23:59:59'), ('헬스하기', '2023-11-21 23:59:59'), ('헬스하기', '2023-11-28 23:59:59');”`
    },
    {
        "role": "user",
        "content": "3시간 후에 밥먹기"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time) VALUES ('밥먹기', '2023-11-23 23:25:39');"`
    },
    {
        "role": "user",
        "content": "키보드 사기를 스케쥴로 바꿔줘."
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO SCHEDULE (title, start_time, end_time, memo, category_id, priority) SELECT title, due_time, due_time, memo, category_id, priority FROM TODO WHERE id=3; DELETE FROM TODO WHERE id=3"`
    },
    {
        "role": "user",
        "content": "신영이랑 밥먹기를 투두로 바꿔줘."
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time, memo, category_id, priority) SELECT title, start_time, memo, category_id, priority FROM SCHEDULE WHERE id=1; DELETE FROM SCHEDULE WHERE id=1;"`
    },
    {
        "role": "user",
        "content": "이번달 과제 모두 삭제해줘."
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE category_id=1 AND date(due_time) LIKE '2023-11%';"`
    },
    {
        "role": "user",
        "content": "오늘 남은거 삭제해줘"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE due_time>'2023-11-23 20:25:39' AND date(due_time)='2023-11-23'; DELETE FROM SCHEDULE WHERE start_time>'2023-11-23 20:25:39' AND date(end_time)='2023-11-23';"`
    },
    {
        "role": "user",
        "content": "이번주 목요일에 알골 과제 추가"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time, category_id) VALUES ('알골 과제', '2023-11-23 23:59:59', 1);"`
    },
    {
        "role": "user",
        "content": "밥먹기 3시간 이후에 공과금 내기"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time) SELECT '공과금 내기', datetime(due_time, '+3 hours') FROM TODO WHERE id=2;"`
    },
    {
        "role": "user",
        "content": "내일 아침에 밥먹기"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time) VALUES ('밥먹기', '2023-11-24 08:00:00');"`
    }
]



app.post('/manager/send', async (req, res) => {
    
    const userMessage = req.body.message;
    const category_list = req.body.category;
    const todo_list = req.body.todo;
    const schedule_list=req.body.schedule;
    const time = req.body.time;

    const messages=[
        {
            role: "system",
            content: systemPrompt 
        }]
    
    messages.push(...assistant_exemplars);

    const userData = `Current time: [${time}]
    
    CATEGORY(id, title): ${category_list}

    TODO(id, title): ${todo_list}

    SCHEDULE(id, title): ${schedule_list}
    `
    
    messages.push({
        role: "user",
        content: userData
    },
    {
        role: "user",
        content: userMessage
    })
    


    try {
        const response = await axios.post(OPENAI_API_URL, {
            model: "gpt-4",
            messages: messages,
            temperature: 0
        }, {
            headers: {
                'Authorization': `Bearer ${OPENAI_API_KEY}`,
                'Content-Type': 'application/json'
            }
        });

        const gptResponse = response.data.choices[0].message.content;
        //gptResponse is a string-formatted json with array of string sql queries in attribute "queries"
        res.send(gptResponse); 

    } catch (error) {
        console.error('Error querying OpenAI:', error.response.data);
        res.status(500).json({ error: 'Failed to get a response from OpenAI' });
    }
});



app.post('/manager/briefing', async (req, res) => {
    const plansString = req.body.plansString;
    const time = req.body.time;
    const userMessage=`Current Time: ${time}
    ${plansString}`
   
   
    const systemPrompt = `SCHEDULE is an event that has both start time & end time. TODO is a task that only has due time. The term PLAN includes both TODO and SCHEDULE. Priority is from 1~5, higher the more important. In TODO, complete is a boolean value that represents whether TODO is complete or not.

    You will be given records from SCHEDULE and TODO tables. As the user's personal manager, your job is to give a brief summary of the user's plans in Korean. Start with "매니저가 찾은 계획들을 요약해드릴게요!". End with encouraging text. Do not output any other text.
    
    Be as friendly and casual as possible. Omit unimportant information. Do not include year when addressing time unless it is  far from current time. Try not to exceed 500 characters including blank spaces. `
    
    const messages=[
        {
            role: "system",
            content: systemPrompt 
        },
        {
            role: "user",
            content: `Current Time:2023-10-21 09:00:00
            Todo(title=안녕, dueTime=Tue Dec 05 20:10:52 GMT+09:00 2023, complete=false, memo=, category=만남, priority=3), Todo(title=시험, dueTime=Thu Dec 07 20:11:15 GMT+09:00 2023, complete=false, memo=, category=학교, priority=4), Schedule(title=수업, startTime=Wed Dec 06 14:11:00 GMT+09:00 2023, endTime=Sat Dec 02 15:11:00 GMT+09:00 2023, memo=, category=None, priority=4)`

        },
        {
            role: "assistant",
            content:`매니저가 찾은 계획들을 요약해드릴게요!

먼저, 12월 5일 20시 10분에 '안녕'이라는 만남이 예정되어 있어요. 그리고 12월 7일 20시 11분에는 '시험'이 있으니 꼭 준비하세요! 그리고 12월 6일에는 14시 11분부터 수업이 있어요.
            
이 모든 일정들을 잊지 않으시길 바라요. 화이팅하세요!`

        }
    ]

    messages.push({
        role: "user",
        content: userMessage 
    })
    


    try {
        const response = await axios.post(OPENAI_API_URL, {
            model: "gpt-4",
            messages: messages,
            temperature: 0
        }, {
            headers: {
                'Authorization': `Bearer ${OPENAI_API_KEY}`,
                'Content-Type': 'application/json'
            }
        });

        const gptResponse = response.data.choices[0].message.content;
        //gptResponse is a string-formatted json with array of string sql queries in attribute "queries"
        res.send(gptResponse); 

    } catch (error) {
        console.error('Error querying OpenAI:', error.response.data);
        res.status(500).json({ error: 'Failed to get a response from OpenAI' });
    }
});




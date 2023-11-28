package com.example.calendy.view.messageview

import LoadingAnimation2
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendy.AppViewModelProvider
import com.example.calendy.R
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.ui.theme.Blue3
import com.example.calendy.utils.equalDay
import com.example.calendy.utils.toDateDayString
import com.example.calendy.view.messagepage.MessagePageViewModel
import java.util.Date

@Composable
fun MessagePage(
    messagePageViewModel: MessagePageViewModel,
) {
    val messageUiState: MessageUIState by messagePageViewModel.uiState.collectAsState()
    val userInput = messageUiState.userInputText
    val messageLogList: List<Message> = messageUiState.messageLogs

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        MessageList(
            messageLogList, modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        MessageInputField(
            onValueChanged = messagePageViewModel::setUserInputText,
            onSendButtonClicked = messagePageViewModel::onSendButtonClicked,
            onMicButtonClicked = messagePageViewModel::onMicButtonClicked,
            text = userInput,
            isListening = messageUiState.isMicButtonClicked,
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputField(
    onSendButtonClicked: () -> Unit = {},
    onMicButtonClicked: (Context) -> Unit = {},
    onValueChanged: (text: String) -> Unit = {},
    text: String = "",
    isListening: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // This is the launcher for permission request
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("GUN", "MIC PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("GUN", "MIC PERMISSION DENIED")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF1F1F1))
    ) {
        IconButton(onClick = {
            when (PackageManager.PERMISSION_GRANTED) { // Check permission
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                )    -> {
                    // Permission OK
                    onMicButtonClicked(context)
                }

                else -> {
                    // NO Permission
                    // Use launcher to ask for permission
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }) {
            if (!isListening) {
                Icon(
                    imageVector = Icons.Default.Mic, contentDescription = "Start Speech Recognition"
                )
            } else {
//                Icon(
//                    imageVector = Icons.Default.MoreHoriz,
//                    contentDescription = "Cancel Speech Recognition"
//                )
                LoadingAnimation2(
                    circleColor= Blue3,
                    circleSize= 8.dp,
                    spaceBetween=4.dp,
                    animationDelay= 400,
                    initialAlpha= 0.3f
                )
            }
        }

        BasicTextField(
            value = text,
            onValueChange = onValueChanged,
            enabled= !isListening,
            modifier = modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(0xFFFFFEFE), shape = RoundedCornerShape(size = 25.dp))
                .then(Modifier.padding(horizontal = 12.dp, vertical = 5.dp)), // Remove padding
            textStyle = TextStyle(
                color = Color.Black, fontSize = 16.sp
            ),
            maxLines = 5
        )

        IconButton(
            onClick = onSendButtonClicked,
            modifier = Modifier
//                .padding(end = 10.dp)
                .wrapContentWidth()
                .wrapContentHeight(),

            ) {
            Icon(painter = painterResource(id = R.drawable.send), "send")
        }

    }
}

@Composable
fun MessageList(
    messageLogList: List<Message> = emptyList(), modifier: Modifier = Modifier
) {
    var prevDate: Date? = null

    if (!messageLogList.isEmpty()) {
        prevDate = messageLogList[0].sentTime
    }

    // wrap content with ListItemWrapper, and add time divider if needed
    val listItems = mutableListOf<@Composable ()->Unit>()
    for( msg in messageLogList){
        var currDate = msg.sentTime
        if ( prevDate != null && !(prevDate!!.equalDay(currDate))) {
            listItems.add { DateDivider(prevDate) } // since messages are sorted in descending order, add prevDate
            prevDate = currDate
        }
        listItems.add { MessageItem(msg) }
    }
    if (prevDate != null) {
        // first date divider
        listItems.add { DateDivider(prevDate) }
    }
    
    LazyColumn(
        reverseLayout = true, modifier = modifier.fillMaxWidth()

    ) {
        items(listItems) {
            it()
        }
    }
}

@Composable
fun MessageItem(messageLog: Message, modifier: Modifier = Modifier) {
    val fromManager= messageLog.messageFromManager
    val chatBackground = when (fromManager) {
        true  -> Color(0xFFACC7FA)
        false -> Color(0xFFDBE2F6)
    }
    Row(
        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = when (fromManager) {
            true  -> Arrangement.Start
            false -> Arrangement.End
        }
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(5.dp)
        ){
            if (!fromManager) {
                SentTime(messageLog)
            }
            Box(
                modifier = Modifier
                    .widthIn(20.dp, 320.dp)
                    .wrapContentWidth()
                    .padding(vertical = 10.dp, horizontal =5.dp)
                    .background(
                        color = chatBackground,
                        shape = RoundedCornerShape(size = 15.dp)
                    )
                    //            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                    .clip(shape = RoundedCornerShape(15.dp))
            ) {
                when (fromManager) {
                    true -> MessageContentManager(messageLog)
                    false -> MessageContentUser(messageLog)
                }
            }

            if (fromManager) {
                SentTime(messageLog)
            }

        }
    }


}


@Composable
fun DateDivider(
    date: Date? = null, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .weight(1f),
            color = Color.Gray // Optional: You can set the color of the divider
        )
        Text(
            text = date!!.toDateDayString(),
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .weight(1f),
            color = Color.Gray // Optional: You can set the color of the divider
        )
    }
}

//@Preview(showBackground = false, name = "Message Page Preview")
//@Composable
//fun MessagePagePreview() {
//    MessagePage()
//}

@Preview(showBackground = false, name = "chatbar preview")
@Composable
fun MessageInputPreview() {
    MessageInputField(isListening=true)
}


@Preview(showBackground = false, name = "chat item preview")
@Composable
fun MessageItemPreview() {
    Column() {
        MessageItem(
            Message(
                0,
                Date(),
                false,
                "ooooooooxoxoxooxoxooxooxoxoxooxoxoxooxoxxoxoxoxoxoxoxoxoxo",
                false
            )
        )
        MessageItem(
            Message(
                1, Date(), true, "AI_THINKING", hasRevision = true
            )
        )
    }
}


@Preview
@Composable
fun DividerPreview() {
    DateDivider(Date())
}
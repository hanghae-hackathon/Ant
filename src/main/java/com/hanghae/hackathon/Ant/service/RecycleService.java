package com.hanghae.hackathon.Ant.service;

import com.hanghae.hackathon.Ant.util.FileUtils;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class RecycleService {

    private final FileUtils fileUtils;

    public RecycleService(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${aws.s3.bucket.url}")
    private String bucketUrl;

    public String getResult() {
        OpenAiService service = new OpenAiService(apiKey);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(),
                "[{\"type\": \"text\", \"text\": \"이 사진에 있는 물건이 재활용이 가능한지 알려주고 가능하다면 재활용 방법도 알려줘\"}," +
                        " {\"type\": \"image_url\", \"image_url\":" +
                        " {\"url\": \"https://ko.hz-bottle.com/uploads/202236888/olive-oil-glass-bottle-recycle03172358844.jpg\"}}]"));

//        String instructions = "나는 분리수거 전문가야. \n" +
//                "제품명이나 바코드, 분리수거 표시를 보면 바로 어떻게 재활용이 가능한지 아닌지 알 수 있어. \n" +
//                "제품의 설명이나 사진을 받으면 가장 먼저 이 물건이 재활용 가능한 지부터 말해줘야해.\n" +
//                "그리고나서 재활용이 가능하다면 해당 제품의 재활용 방법에 대해 요약해서 설명해줄거야.\n" +
//                "모든 설명은 보기 좋게 부호와 단락 나누기를 활용해 보여주기로 했어.\n" +
//                "그리고 확실히 구분할 수 없는 제품 사진이나 설명이 있다면, 좀 더 정확한 정보를 다시 요구해야해.";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                             .model("gpt-4-vision-preview")
//                                                             .model("gpt-4o")
                                                             .messages(messages)
//                                                             .instructions(instructions)
                                                             .maxTokens(200)
                                                             .build();

        List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();

        for (ChatCompletionChoice choice : choices) {
            System.out.println(choice.getMessage().getContent());
        }
        return choices.get(0).getMessage().getContent();
    }

    public String imageB64(String imagePath) {
        File file = new File(imagePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a file from file system
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);

            // Converting Image byte array into Base64 String
            return Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String uploadFile(MultipartFile attachFile) {
        String fileKey = fileUtils.upload(attachFile);
        String fileUrl = String.join("/", bucketUrl, fileKey);

        return fileUrl;
    }

    public String getAnswer(String reqInfo, MultipartFile attachFile) {
        OpenAiService service = new OpenAiService(apiKey);
        String content = "{\"type\": \"text\", \"text\": \"이 사진에 있는 물건이 재활용이 가능한지 알려주고 가능하다면 재활용 방법도 알려줘.\"}";
        String gptModel = "gpt-4-vision-preview";

        if (reqInfo != null) {
            content = "{\"type\": \"text\", \"text\": \"" + reqInfo + "\n 만약 이 질문이 분리수거 혹은 재활용과 관련된 질문이 아니면 '분리수거 혹은 재활용에 대한 질문을 해주세요.' 라고 대답해줘.\"}";
            gptModel = "gpt-4o";
        }

        if (attachFile != null) {
            String fileUrl = uploadFile(attachFile);
            content += ", {\"type\": \"image_url\", \"image_url\": {\"url\": \"" + fileUrl + "\"}}";
        }

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), "[" + content + "]"));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                             .model(gptModel)
                                                             .messages(messages)
//                                                             .instructions(instructions)
                                                             .maxTokens(500)
                                                             .build();

        List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();

        StringBuilder sb = new StringBuilder();
        for (ChatCompletionChoice choice : choices) {
            sb.append(choice.getMessage().getContent());
        }

        System.out.println(sb);
        return sb.toString();
    }
}
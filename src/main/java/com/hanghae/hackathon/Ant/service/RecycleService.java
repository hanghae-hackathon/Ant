package com.hanghae.hackathon.Ant.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class RecycleService {

    @Value("${openai.api-key}")
    private String apiKey;

    public String getResult() {
        OpenAiService service = new OpenAiService(apiKey);
//        FunctionExecutor functionExecutor = new FunctionExecutor(functionList);
        String filePath = "C:\\Users\\taehan\\Desktop\\project\\Ant\\src\\main\\resources\\static\\img\\hot.jpg";

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), getPayload("이 사진 속 사물에 대해 설명해줘.", imageB64(filePath)));
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4o-2024-05-13")
                .messages(messages)
//                .functions(functionExecutor.getFunctions())
//                .functionCall(new ChatCompletionRequest.ChatCompletionRequestFunctionCall("auto"))
                .maxTokens(256)
                .build();

        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
//        ChatFunctionCall functionCall = responseMessage.getFunctionCall(); // might be null, but in this case it is certainly a call to our 'get_weather' function.

//        ChatMessage functionResponseMessage = functionExecutor.executeAndConvertToMessageHandlingExceptions(functionCall);
//        messages.add(response);
        System.out.println(responseMessage.getContent());
        return responseMessage.getContent();



    }

    public String getPayload(String message, String imageBase64) {
        return "[{\"type\": \"text\"" +
                ",\"text\": \"" + message + "\"}" +
                ",\"{\"type\": \"image_url\"" +
                    ",\"image_url\": {\"url\": \"<http://52.78.158.250:8088/testview>\"}\"}\"]\"";

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

}

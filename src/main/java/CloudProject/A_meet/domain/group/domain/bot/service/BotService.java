//package CloudProject.A_meet.domain.group.domain.bot.service;
//
//import CloudProject.A_meet.domain.group.domain.bot.domain.Bot;
//import CloudProject.A_meet.domain.group.domain.bot.domain.BotType;
//import CloudProject.A_meet.domain.group.domain.bot.dto.BotResponse;
//import CloudProject.A_meet.domain.group.domain.bot.repository.BotRepository;
//import CloudProject.A_meet.domain.group.domain.meeting.domain.Meeting;
//import CloudProject.A_meet.domain.group.domain.meeting.repository.MeetingRepository;
//import CloudProject.A_meet.domain.group.domain.note.domain.Note;
//import CloudProject.A_meet.domain.group.domain.note.dto.NoteResponse;
//import CloudProject.A_meet.domain.group.domain.note.repository.NoteRepository;
//import CloudProject.A_meet.global.common.error.exception.CustomException;
//import CloudProject.A_meet.global.common.error.exception.ErrorCode;
//import CloudProject.A_meet.infra.service.S3Service;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Map;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class BotService {
//    private final BotRepository botRepository;
//    private final MeetingRepository meetingRepository;
//    private final S3Service s3service;
//    private final NoteRepository noteRepository;
//
//    public BotResponse summaryBot(Long meetingId) {
//        Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
//
//        Bot bot = Bot.builder()
//                .meetingId(meeting)
//                .type(BotType.SUMMARY)
//                .content("Summary content")
//                .build();
//
//        Bot savedBot = botRepository.save(bot);
//
//        // FastAPI 서버 URL
//        String apiUrl = "http://127.0.0.1:8000/api/summary";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String transcriptionText = "우리가 다음 분기 동안 어떤 제품을 출시할지에 대한 아이디어를 모으는 회의입니다. 우선 각자 생각해본 아이디어나 방향성을 공유해 주세요. 저는 최근에 AI를 활용한 기능을 추가하면 좋겠다고 생각했습니다. 예를 들어, 사용자 맞춤형 추천 시스템을 구현해서 고객 경험을 개선할 수 있지 않을까요? 그 아이디어 좋네요. 사용자 맞춤형 추천 시스템을 디자인할 때는 UI가 굉장히 중요한데, 직관적이고 사용자가 쉽게 접근할 수 있어야 할 것 같아요. 그래서 추천 아이템을 어떻게 보여줄지에 대한 디자인 아이디어를 생각해봤습니다. 모듈식 카드 형태로 디자인하면 좋을 것 같아요. 추천 시스템이 고객 경험을 개선하는 데 중요한 역할을 할 수 있다는 점에서 동의합니다. 그런데 마케팅 측면에서 중요한 건, 그 추천 시스템을 어떻게 우리 브랜드의 가치와 연결시킬 수 있을지 생각해봐야 할 것 같습니다. 예를 들어, 고객의 관심사나 이전 구매 패턴을 분석해서 좀 더 개인화된 마케팅 메시지를 전달하는 거죠. 그렇다면 그 데이터를 어떻게 수집하고 분석할지도 중요한 부분이겠네요. 예를 들어, 사용자가 앱 내에서 무엇을 자주 클릭하거나 어떤 페이지를 방문하는지 추적하는 방법을 사용해야 할 것 같습니다. 이 데이터를 바탕으로 추천 시스템을 더욱 정확하게 만들 수 있을 것 같습니다.";
//
//        // 요청 본문 생성
//        String requestBody = "{ \"script\": \"" + transcriptionText.replace("\"", "\\\"") + "\" }";
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        String summary = "응답을 가져올 수 없습니다."; // 기본 값
//
//        try {
//            // JSON을 파싱하여 "response" 키의 값을 가져옴
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, String> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
//            summary = responseMap.getOrDefault("response", "응답을 가져올 수 없습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//         bot.updateContent(summary);
//         botRepository.save(bot);
//
//        return new BotResponse(meetingId, savedBot.getBotId(), summary);
//    }
//
//
//    private String extractTranscriptionText(String jsonResponse) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(jsonResponse);
//            JsonNode transcriptsNode = rootNode.path("results").path("transcripts");
//
//            if (transcriptsNode.isArray() && transcriptsNode.size() > 0) {
//                return transcriptsNode.get(0).path("transcript").asText();
//            } else {
//                throw new CustomException(ErrorCode.TRANSCRIPTION_TEXT_NOT_FOUND);
//            }
//        } catch (Exception e) {
//            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
//        }
//    }
//
//    private String extractS3KeyFromPresignedUrl(String presignedUrl) {
//        try {
//            URL url = new URL(presignedUrl);
//            String path = url.getPath();
//            return path.startsWith("/") ? path.substring(1) : path;
//        } catch (MalformedURLException e) {
//            throw new CustomException(ErrorCode.INVALID_PRESIGNED_URL);
//        }
//    }
//
//
//    public BotResponse positiveBot(Long meetingId) {
//        Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
//
//        Bot bot = Bot.builder()
//                .meetingId(meeting)
//                .type(BotType.POSITIVE)
//                .content("Positive content")
//                .build();
//
//        Bot savedBot = botRepository.save(bot);
//
//        // FastAPI 서버 URL
//        String apiUrl = "http://127.0.0.1:8000/api/positive";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String transcriptionText = "우리가 다음 분기 동안 어떤 제품을 출시할지에 대한 아이디어를 모으는 회의입니다. 우선 각자 생각해본 아이디어나 방향성을 공유해 주세요. 저는 최근에 AI를 활용한 기능을 추가하면 좋겠다고 생각했습니다. 예를 들어, 사용자 맞춤형 추천 시스템을 구현해서 고객 경험을 개선할 수 있지 않을까요? 그 아이디어 좋네요. 사용자 맞춤형 추천 시스템을 디자인할 때는 UI가 굉장히 중요한데, 직관적이고 사용자가 쉽게 접근할 수 있어야 할 것 같아요. 그래서 추천 아이템을 어떻게 보여줄지에 대한 디자인 아이디어를 생각해봤습니다. 모듈식 카드 형태로 디자인하면 좋을 것 같아요. 추천 시스템이 고객 경험을 개선하는 데 중요한 역할을 할 수 있다는 점에서 동의합니다. 그런데 마케팅 측면에서 중요한 건, 그 추천 시스템을 어떻게 우리 브랜드의 가치와 연결시킬 수 있을지 생각해봐야 할 것 같습니다. 예를 들어, 고객의 관심사나 이전 구매 패턴을 분석해서 좀 더 개인화된 마케팅 메시지를 전달하는 거죠. 그렇다면 그 데이터를 어떻게 수집하고 분석할지도 중요한 부분이겠네요. 예를 들어, 사용자가 앱 내에서 무엇을 자주 클릭하거나 어떤 페이지를 방문하는지 추적하는 방법을 사용해야 할 것 같습니다. 이 데이터를 바탕으로 추천 시스템을 더욱 정확하게 만들 수 있을 것 같습니다.";
//
//        // 요청 본문 생성
//        String requestBody = "{ \"script\": \"" + transcriptionText.replace("\"", "\\\"") + "\" }";
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        String summary = "응답을 가져올 수 없습니다."; // 기본 값
//
//        try {
//            // JSON을 파싱하여 "response" 키의 값을 가져옴
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, String> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
//            summary = responseMap.getOrDefault("response", "응답을 가져올 수 없습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        bot.updateContent(summary);
//        botRepository.save(bot);
//
//        return new BotResponse(meetingId, savedBot.getBotId(), summary);
//    }
//
//    public BotResponse negativeBot(Long meetingId) {
//        Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
//
//        Bot bot = Bot.builder()
//                .meetingId(meeting)
//                .type(BotType.NEGATIVE)
//                .content("Negative content")
//                .build();
//
//        Bot savedBot = botRepository.save(bot);
//
//        // FastAPI 서버 URL
//        String apiUrl = "http://127.0.0.1:8000/api/negative";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String transcriptionText = "우리가 다음 분기 동안 어떤 제품을 출시할지에 대한 아이디어를 모으는 회의입니다. 우선 각자 생각해본 아이디어나 방향성을 공유해 주세요. 저는 최근에 AI를 활용한 기능을 추가하면 좋겠다고 생각했습니다. 예를 들어, 사용자 맞춤형 추천 시스템을 구현해서 고객 경험을 개선할 수 있지 않을까요? 그 아이디어 좋네요. 사용자 맞춤형 추천 시스템을 디자인할 때는 UI가 굉장히 중요한데, 직관적이고 사용자가 쉽게 접근할 수 있어야 할 것 같아요. 그래서 추천 아이템을 어떻게 보여줄지에 대한 디자인 아이디어를 생각해봤습니다. 모듈식 카드 형태로 디자인하면 좋을 것 같아요. 추천 시스템이 고객 경험을 개선하는 데 중요한 역할을 할 수 있다는 점에서 동의합니다. 그런데 마케팅 측면에서 중요한 건, 그 추천 시스템을 어떻게 우리 브랜드의 가치와 연결시킬 수 있을지 생각해봐야 할 것 같습니다. 예를 들어, 고객의 관심사나 이전 구매 패턴을 분석해서 좀 더 개인화된 마케팅 메시지를 전달하는 거죠. 그렇다면 그 데이터를 어떻게 수집하고 분석할지도 중요한 부분이겠네요. 예를 들어, 사용자가 앱 내에서 무엇을 자주 클릭하거나 어떤 페이지를 방문하는지 추적하는 방법을 사용해야 할 것 같습니다. 이 데이터를 바탕으로 추천 시스템을 더욱 정확하게 만들 수 있을 것 같습니다.";
//
//        // 요청 본문 생성
//        String requestBody = "{ \"script\": \"" + transcriptionText.replace("\"", "\\\"") + "\" }";
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        String summary = "응답을 가져올 수 없습니다."; // 기본 값
//
//        try {
//            // JSON을 파싱하여 "response" 키의 값을 가져옴
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, String> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
//            summary = responseMap.getOrDefault("response", "응답을 가져올 수 없습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        bot.updateContent(summary);
//        botRepository.save(bot);
//
//        return new BotResponse(meetingId, savedBot.getBotId(), summary);
//    }
//
//    public BotResponse loaderBot(Long meetingId) {
//        Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
//
//        Bot bot = Bot.builder()
//                .meetingId(meeting)
//                .type(BotType.NEGATIVE)
//                .content("Negative content")
//                .build();
//
//        Bot savedBot = botRepository.save(bot);
//
//        // FastAPI 서버 URL
//        String apiUrl = "http://127.0.0.1:8000/api/loader";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String transcriptionText = "우리가 다음 분기 동안 어떤 제품을 출시할지에 대한 아이디어를 모으는 회의입니다. 우선 각자 생각해본 아이디어나 방향성을 공유해 주세요. 저는 최근에 AI를 활용한 기능을 추가하면 좋겠다고 생각했습니다. 예를 들어, 사용자 맞춤형 추천 시스템을 구현해서 고객 경험을 개선할 수 있지 않을까요? 그 아이디어 좋네요. 사용자 맞춤형 추천 시스템을 디자인할 때는 UI가 굉장히 중요한데, 직관적이고 사용자가 쉽게 접근할 수 있어야 할 것 같아요. 그래서 추천 아이템을 어떻게 보여줄지에 대한 디자인 아이디어를 생각해봤습니다. 모듈식 카드 형태로 디자인하면 좋을 것 같아요. 추천 시스템이 고객 경험을 개선하는 데 중요한 역할을 할 수 있다는 점에서 동의합니다. 그런데 마케팅 측면에서 중요한 건, 그 추천 시스템을 어떻게 우리 브랜드의 가치와 연결시킬 수 있을지 생각해봐야 할 것 같습니다. 예를 들어, 고객의 관심사나 이전 구매 패턴을 분석해서 좀 더 개인화된 마케팅 메시지를 전달하는 거죠. 그렇다면 그 데이터를 어떻게 수집하고 분석할지도 중요한 부분이겠네요. 예를 들어, 사용자가 앱 내에서 무엇을 자주 클릭하거나 어떤 페이지를 방문하는지 추적하는 방법을 사용해야 할 것 같습니다. 이 데이터를 바탕으로 추천 시스템을 더욱 정확하게 만들 수 있을 것 같습니다.";
//
//        // 요청 본문 생성
//        String requestBody = "{ \"script\": \"" + transcriptionText.replace("\"", "\\\"") + "\" }";
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        String summary = "응답을 가져올 수 없습니다."; // 기본 값
//
//        try {
//            // JSON을 파싱하여 "response" 키의 값을 가져옴
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, String> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
//            summary = responseMap.getOrDefault("response", "응답을 가져올 수 없습니다.");
//            System.out.println("찍혔나");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        bot.updateContent(summary);
//        botRepository.save(bot);
//
//        return new BotResponse(meetingId, savedBot.getBotId(), summary);
//    }
//
//    public void attendanceBot(Long meetingId) {
//        Meeting meeting = meetingRepository.findById(meetingId)
//            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
//        Bot bot = Bot.builder()
//            .meetingId(meeting)
//            .type(BotType.ATTENDANCE)
//            .content(null)
//            .build();
//        botRepository.save(bot);
//    }
//
//    public NoteResponse createNote(Long noteId) {
//        Note note = transactionalFindByNoteId(noteId);
//
//        String presignedUrl = note.getAudioUrl();
//        String bucketName = "meeting-input-cp";
//        String objectKey = extractS3KeyFromPresignedUrl(presignedUrl);
//        String s3Uri = "s3://" + bucketName + "/" + objectKey;
//
//        String keyName = "note" + note.getNoteId();
//
//        String summary = "회의록 생성 예시";
//
//        updateNoteContent(note, summary);
//        return NoteResponse.of(note);
//    }
//
//    public NoteResponse endMeeting(Long noteId) {
//        Note note = transactionalFindByNoteId(noteId);
//
//        // FastAPI 서버 URL
//        String apiUrl = "http://127.0.0.1:8000/api/endMeeting";
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String transcriptionText = "우리가 다음 분기 동안 어떤 제품을 출시할지에 대한 아이디어를 모으는 회의입니다. 우선 각자 생각해본 아이디어나 방향성을 공유해 주세요. 저는 최근에 AI를 활용한 기능을 추가하면 좋겠다고 생각했습니다. 예를 들어, 사용자 맞춤형 추천 시스템을 구현해서 고객 경험을 개선할 수 있지 않을까요? 그 아이디어 좋네요. 사용자 맞춤형 추천 시스템을 디자인할 때는 UI가 굉장히 중요한데, 직관적이고 사용자가 쉽게 접근할 수 있어야 할 것 같아요. 그래서 추천 아이템을 어떻게 보여줄지에 대한 디자인 아이디어를 생각해봤습니다. 모듈식 카드 형태로 디자인하면 좋을 것 같아요. 추천 시스템이 고객 경험을 개선하는 데 중요한 역할을 할 수 있다는 점에서 동의합니다. 그런데 마케팅 측면에서 중요한 건, 그 추천 시스템을 어떻게 우리 브랜드의 가치와 연결시킬 수 있을지 생각해봐야 할 것 같습니다. 예를 들어, 고객의 관심사나 이전 구매 패턴을 분석해서 좀 더 개인화된 마케팅 메시지를 전달하는 거죠. 그렇다면 그 데이터를 어떻게 수집하고 분석할지도 중요한 부분이겠네요. 예를 들어, 사용자가 앱 내에서 무엇을 자주 클릭하거나 어떤 페이지를 방문하는지 추적하는 방법을 사용해야 할 것 같습니다. 이 데이터를 바탕으로 추천 시스템을 더욱 정확하게 만들 수 있을 것 같습니다.";
//
//        // 요청 본문 생성
//        String requestBody = "{ \"script\": \"" + transcriptionText.replace("\"", "\\\"") + "\" }";
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                apiUrl, HttpMethod.POST, requestEntity, String.class);
//
//        String summary = "응답을 가져올 수 없습니다.";
//
//        try {
//            // JSON을 파싱하여 "response" 키의 값을 가져옴
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, String> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
//            summary = responseMap.getOrDefault("response", "응답을 가져올 수 없습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 회의록 업데이트
//        note.updateScript(transcriptionText);
//        note.updateSummary(summary);
//        noteRepository.save(note);
//        return NoteResponse.of(note);
//    }
//
//    @Transactional
//    public Note transactionalFindByNoteId(Long noteId) {
//        return noteRepository.findByNoteId(noteId)
//            .orElseThrow(() -> new CustomException(ErrorCode.NOTE_NOT_FOUND));
//    }
//
//    @Transactional
//    public void updateNoteContent(Note note, String summary) {
//        note.updateSummary(summary);
//    }
//
//}

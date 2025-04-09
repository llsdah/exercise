package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SNSApplicationException;
import com.fastcampus.sns.model.AlarmArguments;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.EmitterRepository;
import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.AlarmEntityRepositoty;
import com.fastcampus.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 다중 인스턴스인 경우 -> 알람을 전체 인스턴스에 알려주고 -> 해당 인스턴스가 해당 user에서 알람을 준다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {


    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepositoty alarmEntityRepositoty;
    private final UserEntityRepository userEntityRepository;

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 알람 저장
        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() ->emitterRepository.delete(userId));
        sseEmitter.onTimeout(( )->emitterRepository.delete(userId));

        try {
            // 데이터 자체 분석 x -> 이벤트 수행시 리프레쉬
            // 이벤트의 이름!
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
        }catch (IOException exception) {
            throw new SNSApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

    // 이벤트 보낼시 어떻게 보내야 할까 
    // 서버 1대에 여러 클라이언트 해당 서버 정보 저장 필요
    //public void sender(Integer alarmId,Integer userId){
    public void sender(AlarmType alarmType, AlarmArguments args, Integer receiverUserId){

        // FIXME kafka 작업
        UserEntity userEntity = userEntityRepository.findById(receiverUserId).orElseThrow(()
                -> new SNSApplicationException(ErrorCode.USER_NOT_FOUNDES));
        AlarmEntity alarmEntity = alarmEntityRepositoty.save(AlarmEntity.of(userEntity,alarmType, args));


        // SSE
        // 서버에 한번이라도 접속해야 값이 있음 , null 가능
        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(sseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data(""));
            } catch (IOException e) {
                emitterRepository.delete(receiverUserId);
                throw new SNSApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        },() -> log.info("No emitter founded"));
    }


}

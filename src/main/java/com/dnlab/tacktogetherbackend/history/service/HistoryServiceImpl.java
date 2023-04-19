package com.dnlab.tacktogetherbackend.history.service;

import com.dnlab.tacktogetherbackend.history.domain.History;
import com.dnlab.tacktogetherbackend.history.dto.HistoriesDTO;
import com.dnlab.tacktogetherbackend.history.dto.HistoryDTO;
import com.dnlab.tacktogetherbackend.history.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
//    @Override
//    public HistoriesDTO findHistoriesByUserName(String username) {
//        List<History> histories = historyRepository.findHistoriesByMemberUsername(username); //엔티티 리스트
//        //DTO로 맵핑된(Entity to Dto) 리스트
//        List<HistoryDTO> historyDTOS = new ArrayList<>();
//        for (History history : histories) {
//            HistoryDTO historyDTO = convertHistoryEntityToDTO(history);
//            historyDTOS.add(historyDTO);
//        }
//        return new HistoriesDTO(historyDTOS);
//    }

//    @Override
//    public HistoriesDTO findHistoriesByUserName(String username) {
//        return new HistoriesDTO(historyRepository.findHistoriesByMemberUsername(username)
//                .stream()
//                .map(history -> convertHistoryEntityToDTO(history))
//                .collect(Collectors.toList()));
//    }
    @Override
    public HistoriesDTO findHistoriesByUserName(String username) {
        return new HistoriesDTO(historyRepository.findHistoriesByMemberUsername(username)
                .stream()
                .map(this::convertHistoryEntityToDTO)
                .collect(Collectors.toList()));
    }

    private HistoryDTO convertHistoryEntityToDTO(History history) {
        return HistoryDTO.builder()
                .origin(history.getOrigin())
                .destination(history.getDestination())
                .startTime(history.getStartTime())
                .rideDuration(history.getRideDuration())
                .endTime(history.getEndTime())
                .passengerNickname(String.valueOf(history.getPassengerMember()))
                .savedCost(history.getSavedCost())
                .build();
    }
}

package com.waguwagu.weat.domain.admin.service;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResult;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultDetail;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSettingDetail;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;
    private final CategorySettingRepository categorySettingRepository;
    private final LocationSettingRepository locationSettingRepository;
    private final TextInputSettingRepository textInputSettingRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final AnalysisBasisRepository analysisBasisRepository;

    public void deleteGroupById(String groupId) {

        // 분석 결과 관련 정보 삭제 → basis → detail → result
        List<AnalysisResult> results = analysisResultRepository.findAllByGroupGroupId(groupId);
        for (AnalysisResult result : results) {
            List<AnalysisResultDetail> details = analysisResultDetailRepository.findAllByAnalysisResult(result);
            for (AnalysisResultDetail detail : details) {
                analysisBasisRepository.deleteAllByAnalysisResultDetail(detail);
            }
            analysisResultDetailRepository.deleteAll(details);
        }
        analysisResultRepository.deleteAll(results);

        // 분석 설정 관련 저옵 삭제 (settingDetail → location/text/category → setting)
        List<Member> members = memberRepository.findAllByGroupGroupId(groupId);
        for (Member member : members) {
            List<AnalysisSetting> settings = analysisSettingRepository.findAllByMember(member);
            for (AnalysisSetting setting : settings) {
                List<AnalysisSettingDetail> details = analysisSettingDetailRepository.findAllByAnalysisSetting(setting);
                for (AnalysisSettingDetail detail : details) {
                    categorySettingRepository.deleteById(detail.getAnalysisSettingDetailId());
                    locationSettingRepository.deleteById(detail.getAnalysisSettingDetailId());
                    textInputSettingRepository.deleteById(detail.getAnalysisSettingDetailId());
                    analysisSettingDetailRepository.deleteById(detail.getAnalysisSettingDetailId());
                }
            }
            analysisSettingRepository.deleteAll(settings);
        }
        // 분석 삭제
        analysisRepository.deleteAllByGroupGroupId(groupId);
        // 멤버 삭제
        memberRepository.deleteAllByGroupGroupId(groupId);
        // 그룹 삭제
        groupRepository.deleteById(groupId);
    }
}

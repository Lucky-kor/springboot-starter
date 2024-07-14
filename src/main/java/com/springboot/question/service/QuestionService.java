package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
                           MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

    public void createQuestion(Long memberId, String title, String body, String visibilityStatus) {
        Member verifiedMember = memberService.findVerifiedMember(memberId);

        Question question = new Question();
        question.setMember(verifiedMember);
        question.setTitle(title);
        question.setBody(body);
        question.setCreatedBy(verifiedMember.getEmail());
        question.setCreatedAt(LocalDateTime.now());
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_REGISTERED);
        question.setIsSecret(true);

        if (visibilityStatus.equals("PUBLIC")) {
            question.setVisibilityStatus(Question.VisibilityStatus.PUBLIC);
        } else if (visibilityStatus.equals("SECRET")) {
            question.setVisibilityStatus(Question.VisibilityStatus.SECRET);
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_VISIBILITY_STATUS);
        }
        questionRepository.save(question);
    }

    public Question updateQuestion(Long questionId, Long memberId, String newTitle, String newBody ) {
        Member verifiedMember = memberService.findVerifiedMember(memberId);
        Question question = findVerifiedQuestion(questionId);

        if (!question.getMember().getMemberId().equals(memberId)) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_TO_EDIT);
        }
        if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_ANSWERED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_CANNOT_BE_EDITED);
        }
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            question.setTitle(newTitle);
        }
        if (newBody != null && !newBody.trim().isEmpty()) {
            question.setBody(newBody);
        }

        question.setUpdatedAt(LocalDateTime.now());
        return questionRepository.save(question);
    }

    public Question findQuestion(Long questionId, Long memberId) {
        Member verifiedMember = memberService.findVerifiedMember(memberId);
        Question question = findVerifiedQuestion(questionId);

        if (question.getVisibilityStatus() == Question.VisibilityStatus.SECRET) {
            if (!question.getCreatedBy().equals(verifiedMember.getEmail()) && !isAdmin(verifiedMember)) {
                throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_TO_VIEW);
            }
        }

        if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        }
        return question;
    }

    public Question findQuestions(Long questionId, Long memberId) {
        Member verifiedMember = memberService.findVerifiedMember(memberId);
        Question question = findVerifiedQuestion(questionId);

        if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_ALREADY_DELETED);
        }
        return question;

        // TODO: 각각의 질문에 대한 답변이 있으면 같이 조회
        // TODO: 페이지네이션 처리 : 최신글, 오래된글 순으로 구현
    }

    public void deleteQuestion(Long questionId, Long memberId) {
        Member verifiedMember = memberService.findVerifiedMember(memberId);
        Question question = findVerifiedQuestion(questionId);

        if (!question.getMember().getMemberId().equals(memberId)) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_TO_DELETE);
        }

        if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.QUESTION_ALREADY_DELETED);
        }

        question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);
        questionRepository.save(question);
    }

    public Question findVerifiedQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        return optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    private boolean isAdmin(Member member) {
        return member.getRoles().contains("ROLE_ADMIN");
    }
}

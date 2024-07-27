package com.springboot.member.service;

import com.springboot.auth.utils.JwtAuthorityUtils;
import com.springboot.board.entity.Board;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
// 트랜잭션은 Spring AOP 를 통해 구현되어 있다. 선언적 트랜잭션 처리를 지원.
// 클래스, 매서드에 선언되면 해당 클래스에 트랜잭션이 적용된 프록시 객체 생성.
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthorityUtils authorityUtils;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtAuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    // controller의 Post
    public Member createMember (Member member) {
        // 이메일 검증을 해야 함. 있는 이메일인지 없는 이메일인지.
        verifyExistsEmail(member.getEmail());
        // password 암호화
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        // db에 userRole 저장
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);
        return memberRepository.save(member);
    }

    // controller의 Patch
    public Member updateMember (Member member) {
        // 멤버 아이디를 보고, 있는 멤버인지 아닌지 확인함.
        Member findMember = findVerifedMember(member.getMemberId());
        // 일반 객체를 optional 로 해서 null 값 을 받아주어야 update 가 가능함.
        Optional.ofNullable(member.getName())
                // 만약 존재한다면, set을 사용해서 name 을 수정해라.
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(status -> findMember.setMemberStatus(status));
        findMember.setModifiedAt(LocalDateTime.now());

        return memberRepository.save(findMember);
    }

    // controller 의 Get 은 memberId 를 받아주어야 함.
    public Member findMember (long memberId) {
        // 있는 memberId 인지 확인하는 메서드.
        return findVerifedMember(memberId);
    }

    // 전체조회를 하는 것이기 때문에 페이지 네이션이 필요함.
    public Page<Member> findMembers (int page, int size) {
        // Repository 에서 다 찾아오는데, 페이지네이션을 해야 함.
        // 입력받은 page 와 size 를 기준으로, sort.by 를 해야 함. 그리고 내림차순으로.
        return memberRepository.findAll(PageRequest.of(page, size, Sort.by("memberId").descending()));
    }

    // delete 는 memberId 조회를 해서 없애야 함.
    public void deleteMember (long memberId) {
        // 멤버 아이디로 멤버를 찾고,
        Member findMember = findVerifedMember(memberId);
        // member의 상태를 전환시킨다.
        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        // 질문을 비활성화 한다. 질문은 boards 라서 list 임.
        List<Board> boards = findMember.getBoards();
        for (Board board : boards) {
            board.setQuestionStatus(Board.QuestionStatus.QUESTION_DEACTIVED);
        }
        memberRepository.save(findMember);
    }

    public void verifyExistsEmail (String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }
    public Member findVerifiedMember (String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

    public Member findVerifedMember (long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        // optional 일 때에는 Null 예외처리를 해 주어야 함.
        // 찾은 멤버가 있으면 반환, 없으면 businessLogicException 던지기.
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

}

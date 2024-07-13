package com.springboot.member.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

//@ResponseBody 자바를 제이슨 형태로 전환. @Controller 로 컨트롤러 지정.
@RestController
//특정 url로 요청을 보내면 controller에서 어떠한 방식으로 처리할지 정의.
//이때 들어온 요청을 특정 메서드와 매핑하기 위해 사용되는 어노테이션
@RequestMapping("/v11/members")
//유효성 검증 클래스에 붇임.. 메서드에는 @Valid
@Validated
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/v11/members";

    //의존성 주입.
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }


    @PostMapping
                                      // @RequestBody : 제이슨을 자바로 전환
    public ResponseEntity postMember (@Valid @RequestBody MemberPostDto memberPostDto) {
        // postDto 를 Member 객체로 바꾼다.
        Member member = mapper.memberPostDtoToMember(memberPostDto);
        // 여기에 stamp 객체를 새로 만들어준다.
        // 새롭게 만든 member 를 create 한다.
        Member createdMember = memberService.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());

        return ResponseEntity.created(location).build();

    }

    // patch 할 때에 member id 를 받음.
    // @Validated : 객체의 검증 부분.
    // @PathVariable : 경로 변수를 표시하기 위함.
    // @Positive : 값이 0이 아닌 양수인지 확인.
    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember (@PathVariable("member-id") @Positive long memberId,
                                     @Valid @RequestBody MemberPatchDto memberPatchDto) {

        //memberId 로 원하는 멤버를 찾은 뒤에
        memberPatchDto.setMemberId(memberId);
        // memberPatchDto 를 Member 로 바꿔준다.
        Member updateMember = mapper.memberPatchDtoToMember(memberPatchDto);

        // 바꾼 Member 엔티티로 update 를 한다.
        Member member = memberService.updateMember(updateMember);

        // 응답은 ResponseDto 로 함. OK.
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    // get 할 때에 member id 를 받음.
    @GetMapping("/{member-id}")
    public ResponseEntity getMember (@PathVariable("member-id") @Positive long memberId) {
        // memberId 로 member 를 찾아라.
        Member member = memberService.findMember(memberId);

        // 응답은 ResponseDto 로 함. OK.
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    // 페이지네이션 구현.
    @GetMapping
    public ResponseEntity getMembers (@Positive @RequestParam int page, @Positive @RequestParam int size) {
        // 페이지를 먼저 구현함. 받은 페이지는 -1 을 해서 인덱스 0을 만들어 주어야 함.
        Page<Member> pageMembers = memberService.findMembers(page -1, size);
        // 페이지에 있는 content 만 데려와서 list를 만들어라. 이거는 밑에 있는 ResponseDto 를 만드는데 쓰일 것임.
        List<Member> members = pageMembers.getContent();

        return new ResponseEntity<>(
                // public MultiResponseDto(List<T> data, Page page)
                new MultiResponseDto<>(mapper.membersToMemberResponseDtos(members), pageMembers), HttpStatus.OK
        );
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember (@PathVariable("member-id") @Positive long memberId) {
        // deleteMember 로직 사용.
        memberService.deleteMember(memberId);
        // 반환할 내용은 없고, 상태만 변환시키면 됨.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

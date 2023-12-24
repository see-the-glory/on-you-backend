package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Api(tags = {"Board API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final AwsS3Service awsS3Service;
    private final UserService userService;
    private final ReportService reportService;
    private final BoardService boardService;
    private final LikesService likesService;

    @GetMapping("/api/boards")
    public BoardPageResponse selectBoardList(
            @RequestParam(required = false) String cursor,
            @PageableDefault(sort="created", size = 9) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        return boardService.selectBoardList(pageable, cursor, userId);

    }

//    @GetMapping("/api/boards/my")
//    public FeedPageResponse getMyBoardList(
//            @RequestParam(required = false) String cursor,
//            @PageableDefault(sort="created", size = 9) Pageable pageable,
//            HttpServletRequest httpServletRequest) {
//
//        Long userId = userService.getUserId(httpServletRequest);
//        return boardService.getMyBoardList(pageable, cursor, userId);
//
//    }

    @PostMapping("/api/boards")
    public Header<Object> createBoard(@RequestPart(value = "file") MultipartFile multipartFile,
                                     @RequestPart(value = "boardCreateRequest") BoardCreateRequest request,
                                     HttpServletRequest httpServletRequest
    ) {

        Long userId = userService.getUserId(httpServletRequest);
        String url = awsS3Service.uploadFile(multipartFile);
        boardService.createBoard(request, userId, url);


        return Header.OK("Board 생성 완료");
    }

//    @GetMapping("/api/boards/{boardId}")
//    public Header<BoardResponse> getBoard(@PathVariable Long boardId, HttpServletRequest httpServletRequest) {
//        Long userId = userService.getUserId(httpServletRequest);
//        return boardService.getBoard(userId, boardId);
//    }

    @PutMapping("/api/boards/{id}")
    public Header<String> updateBoard(@PathVariable Long id,
                                     @RequestBody BoardUpdateRequest boardUpdateRequest,
                                     HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);

        boardService.updateBoard(id, boardUpdateRequest);
        return Header.OK("Board 업데이트 완료");

    }

    @DeleteMapping("/api/boards/{id}")
    public Header<Object> deleteBoard(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        boardService.deleteBoard(id, userId);

        return Header.OK("Board 삭제 완료");
    }

    /**
     * BOARD 신고
     */
    @PostMapping("/api/boards/{id}/report")
    public Header<String> reportBoard(@PathVariable Long id, @Valid @RequestBody ReportRequest reportRequest, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);

        String result = reportService.reportBoard(userId, id, reportRequest.getReason());
        return Header.OK(result);
    }

    @PostMapping("/api/boards/{id}/likes")
    public Header<String> likeBoard(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        likesService.addLikesBoard(userId, id);
        return Header.OK("게시판 좋아요/해제 완료");
    }
}
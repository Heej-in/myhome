package com.example.myhome.controller;

import com.example.myhome.model.Board;
import com.example.myhome.repository.BoardRepository;
import com.example.myhome.service.BoardService;
import com.example.myhome.validator.BoardValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardValidator boardValidator;

    @GetMapping("/list")
    private String list(Model model,@PageableDefault(size = 2) Pageable pageable, @RequestParam(required = false, defaultValue = "") String searchText){
//        List<Board> boards = boardRepositroy.findAll();
//        Page<Board> boards = boardRepositroy.findAll(pageable);
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(searchText,searchText,pageable);

        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("boards",boards);
        return "board/list";
    }

    @GetMapping("/form")
    private String form(Model model, @RequestParam(required = false) Long id){
        if(id == null){
            model.addAttribute("board",new Board());
        }else{
            Board board = boardRepository.findById(id).orElse(null);
            model.addAttribute("board",board);
        }
        return "board/form";
    }

    //   handling-form-submission
//    @PostMapping("/form")
//    public String formSubmit(@ModelAttribute Board board){
//        boardRepositroy.save(board);
//        return  "redirect:/board/list";
//    }

    //    handling-form-submission
    @PostMapping("/form")
    public String formSubmit(@Valid Board board, BindingResult bindingResult, Authentication authentication){
        boardValidator.validate(board, bindingResult);
        if(bindingResult.hasErrors()){
            return "board/form";
        }
        //인증정보가져오기
        //Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boardService.save(username,board);
        //boardRepository.save(board);
        return  "redirect:/board/list";
    }
}

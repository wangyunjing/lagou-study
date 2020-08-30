package com.lagou.edu.controller;

import com.lagou.edu.dao.ResumeDao;
import com.lagou.edu.pojo.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/8/30
 */
@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    ResumeDao resumeDao;

    @RequestMapping("/add")
    public ModelAndView add(Resume resume) {
        resumeDao.save(resume);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        List<Resume> all = resumeDao.findAll();
        modelAndView.addObject("resumeList", all);
        return modelAndView;
    }

    @RequestMapping("/update")
    public ModelAndView update(Resume resume) {
        resumeDao.save(resume);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        List<Resume> all = resumeDao.findAll();
        modelAndView.addObject("resumeList", all);
        return modelAndView;
    }

    @RequestMapping("/delete")
    public ModelAndView delete(Long id) {
        resumeDao.deleteById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        List<Resume> all = resumeDao.findAll();
        modelAndView.addObject("resumeList", all);
        return modelAndView;
    }
}

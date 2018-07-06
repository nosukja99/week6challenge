package com.example.week6challenge;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.expression.Sets;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    CarRepository carRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(Model model)
    {
        model.addAttribute("cars", carRepository.findAll());
        return "list";
    }

    @GetMapping("/addcategory")
    public String newCategory(Model model)
    {
        model.addAttribute("category", new Category());
        return "categoryform";
    }

    @PostMapping("/addcategory")
    public String processCategory(@Valid @ModelAttribute Category category, BindingResult result)
    {
        if(result.hasErrors())
        {
            return "categoryform";
        }
        categoryRepository.save(category);
        return "redirect:/";
    }

    @GetMapping("/addcar")
    public String newCar(Model model)
    {
        model.addAttribute("car", new Car());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }

    @PostMapping("/addcar")
    public String processCar(@Valid @ModelAttribute Car car, BindingResult result,
                             @RequestParam("file")MultipartFile file, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("categories", categoryRepository.findAll());
            return "carform";
        }
        if(file.isEmpty())
        {
            model.addAttribute("categories", categoryRepository.findAll());
            return "carform";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            car.setImage(uploadResult.get("url").toString());
            carRepository.save(car);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepository.findById(id).get());
        return "show";
    }


    @RequestMapping("/update/{id}")
    public String updateMeme(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("car", carRepository.findById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "carform";
    }


    @RequestMapping("/delete/{id}")
    public String deleteMeme(@PathVariable("id") long id)
    {
        carRepository.deleteById(id);
        return "redirect:/";
    }

}

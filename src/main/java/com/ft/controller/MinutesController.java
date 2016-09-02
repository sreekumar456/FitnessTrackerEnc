package com.ft.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ft.model.Activity;
import com.ft.model.CryptoMessages;
import com.ft.model.Exercise;
import com.ft.model.Goal;
import com.ft.service.ExerciseService;


@Controller
@SessionAttributes("Crypto")
public class MinutesController {

	@Autowired
	private ExerciseService exerciseService;
	
	@RequestMapping(value = "/addMinutes",  method = RequestMethod.GET)
	public String getMinutes(@ModelAttribute ("exercise") Exercise exercise) {
	
		return "addMinutes";
	}
	
	@RequestMapping(value = "/addMinutes",  method = RequestMethod.POST)
	public String addMinutes(@Valid @ModelAttribute ("exercise") Exercise exercise, BindingResult result) {
		
		System.out.println("exercise: " + exercise.getMinutes());
		System.out.println("exercise activity: " + exercise.getActivity());
		
		if(result.hasErrors()) {
			return "addMinutes";
		}
		
		return "addMinutes";
	}
	
	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	public String encryptCrypto(Model model) {
		
//		Crypto crypto=new Crypto();
		
	/*	Goal goal = new Goal();
		goal.setMinutes(10);
		model.addAttribute("goal", goal);
		*/
		return "crypto";
	}
	
	@RequestMapping(value = "/encryptMessage", method = RequestMethod.POST)
	public String encryptMessage(@Valid @ModelAttribute ("encyptMessage") CryptoMessages encyptMessage, BindingResult result) {
		
		System.out.println("Input Message"+encyptMessage.getEncryptedMessage());
		
//		model.addAttribute("goal", goal);
		
		if(result.hasErrors()) {
			return "crypto";
		}
		
		return "crypto";
	}
	@RequestMapping(value = "/decryptMessage", method = RequestMethod.POST)
	public String decryptMessage(@Valid @ModelAttribute ("decryptMessage") CryptoMessages encyptMessage, BindingResult result) {
		
		/*CryptoMessages crypto=new CryptoMessages();
		
		Goal goal = new Goal();
		goal.setMinutes(10);
		model.addAttribute("goal", goal);*/
		
		return "crypto";
	}
	
}

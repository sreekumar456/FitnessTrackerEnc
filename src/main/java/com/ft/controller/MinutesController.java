package com.ft.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.ft.model.Crypto;
import com.ft.model.CryptoFunctions;
import com.ft.model.Exercise;
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
	
	@RequestMapping("/encrypt")
	public ModelAndView initializeForm()
	{
		Crypto crypto=new Crypto();
		System.out.println("inside");
		return new ModelAndView("crypto","Crypto",new Crypto());
	}
	
/*	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	public String encryptCrypto(Model model) {
		
		Crypto crypto=new Crypto();
		
		model.addAttribute("Crypto", crypto);
		return "crypto";
	}*/
	
	@RequestMapping(value = "/encryptMessage", method = RequestMethod.POST)
	public String encryptMessage(@Valid @ModelAttribute ("Crypto") Crypto encyptMessage, BindingResult result) {
		
		System.out.println("Input Message"+encyptMessage.getInputEncrypt());
		
		CryptoFunctions cryptoFunc=new CryptoFunctions();
		String s=cryptoFunc.encrypt(encyptMessage.getInputEncrypt());
		System.out.println(""+s);	
		encyptMessage.setEncryptedMessage(s);
		System.out.println("model"+encyptMessage.getEncryptedMessage());
		
		if(result.hasErrors()) {
			return "crypto";
		
		}
		
		return "crypto";
	}
	@RequestMapping(value = "/decryptMessage", method = RequestMethod.POST)
	public String decryptMessage(@Valid @ModelAttribute ("Crypto") Crypto decryptMessage, BindingResult result) {
		
		System.out.println("Input Message"+decryptMessage.getInputDecrypt());
		
		CryptoFunctions cryptoFunc=new CryptoFunctions();
		
		String s=cryptoFunc.decrypt(decryptMessage.getInputDecrypt());
		System.out.println(""+s);	
		decryptMessage.setDecrytedMessage(s);
		System.out.println("model"+decryptMessage.getDecrytedMessage());
		
		if(result.hasErrors()) {
			return "crypto";
		
		}
		
		return "crypto";
	}
	
}

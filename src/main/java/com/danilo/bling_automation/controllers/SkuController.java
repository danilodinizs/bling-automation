package com.danilo.bling_automation.controllers;

import com.danilo.bling_automation.services.BlingAutomation;
import com.danilo.bling_automation.services.ExcelReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class SkuController {

    @Autowired
    private ExcelReader excelReader;

    @Autowired
    private BlingAutomation blingAutomation;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/submit")
    public ModelAndView submitSku(@RequestParam String sku) {
        String fileName = "produtos.xlsx"; // Nome do arquivo da planilha no diretório resources
        Map<String, String> productData = excelReader.readExcel(fileName, sku);

        if (!productData.isEmpty()) {
            blingAutomation.createProduct(productData);
            // blingAutomation.close();
        }

        ModelAndView modelAndView = new ModelAndView("result");
        modelAndView.addObject("sku", sku);
        return modelAndView;
    }
}

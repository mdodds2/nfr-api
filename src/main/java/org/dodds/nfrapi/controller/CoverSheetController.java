package org.dodds.nfrapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/coversheets")
public class CoverSheetController {

    @GetMapping
    public String coverSheet(Model model) {
        model.addAttribute("orderid", "12345");
        model.addAttribute("customerName", "12345");

        return "coversheet";
    }
}

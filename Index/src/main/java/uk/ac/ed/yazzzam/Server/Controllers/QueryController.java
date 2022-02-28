package uk.ac.ed.yazzzam.Server.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.yazzzam.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@RestController
public class QueryController {

    @GetMapping("/query")
    public Map<String, Set<Integer>> query(@RequestParam(name="song", required=true) String song, Model model) {
        model.addAttribute("song", song);

        var x = Main.testSearch(song);


        var res = new HashMap<String, Set<Integer>>();

        res.put(song, x);
        return res;
    }

}



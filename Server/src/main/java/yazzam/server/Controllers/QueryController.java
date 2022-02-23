package yazzam.server.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@Controller
//public class GreetingController {
//
//    @GetMapping("/query")
//    public String query(@RequestParam(song="song", required=true) String song, Model model) {
//        model.addAttribute("song", song);
//        return "query";
//    }
//
//}

@Controller
public class QueryController {

    @GetMapping("/query")
    public String query(@RequestParam(name="song", required=true) String song, Model model) {
        model.addAttribute("song", song);
        return "query";
    }

}



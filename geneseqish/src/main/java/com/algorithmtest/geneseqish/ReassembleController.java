package com.algorithmtest.geneseqish;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ReassembleController {

    private final ReassembleService reassembleService;

    public ReassembleController(final ReassembleService reassembleService) {
        this.reassembleService = reassembleService;
    }

    @PostMapping(path = "/reassemble", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String postMethodName(@RequestBody String entity) {
        return reassembleService.reassemble(entity);
    }
    
    
}

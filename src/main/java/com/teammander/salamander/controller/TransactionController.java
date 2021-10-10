package com.teammander.salamander.controller;


import com.teammander.salamander.service.TransactionService;
import com.teammander.salamander.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    TransactionService ts;
    @Autowired
    public TransactionController(TransactionService ts) {
        this.ts = ts;
    }

    public TransactionService getTs() {
        return this.ts;
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        TransactionService ts = getTs();
        List<Transaction> foundTransactions = ts.getAllTransactions();
        return foundTransactions;
    }

    @PostMapping("/{tid}/newComment")
    public void addNewComment(@PathVariable int tid, @RequestBody String comment) {
        TransactionService ts = getTs();
        ts.addComment(tid, comment);
    }

    @PostMapping("/comment/{cid}/updateComment")
    public void updateComment(@PathVariable int cid, @RequestBody String update) {
        TransactionService ts = getTs();
        ts.updateComment(cid, update);
    }

}

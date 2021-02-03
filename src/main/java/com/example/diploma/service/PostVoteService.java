package com.example.diploma.service;

import com.example.diploma.enums.VoteType;

public interface PostVoteService {
    boolean vote(VoteType vote, int postId);
}

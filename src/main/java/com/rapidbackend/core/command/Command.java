package com.rapidbackend.core.command;

/**
 * @author chiqiu
 */
@Deprecated
public enum Command {
    GetFeeds,PostFeed,DeleteFeeds,RepostFeed,UpdateFeed,
    FollowUser,UnfollowUser,SearchFeed,
    HomeTimeline,
    PostComment,UpdateComment,DeleteComment,
    UserInfo,AddUser,DeleteUser,SearchUser,
    FollowingUser,UserPost,UserRepost,Followers,
    unsupported
}
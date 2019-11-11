package edu.buffalo.cse.cse442f19.spotme

class Globals {
    companion object {
        @JvmStatic
        var currentUser: User? = null
        var acceptedUsersOneWay = arrayListOf<User.ScoredUser>()
        var currentAcceptedUsers = arrayListOf<User.ScoredUser>()
        var otherUser1: User.ScoredUser? = null
        var otherUser2: User.ScoredUser? = null
        var otherUser3: User.ScoredUser? = null
        var otherUser4: User.ScoredUser? = null
        var otherUser5: User.ScoredUser? = null
        var otherUser6: User.ScoredUser? = null
        var otherUser7: User.ScoredUser? = null
        var otherUser8: User.ScoredUser? = null
        var otherUser9: User.ScoredUser? = null
        var oustring: String = ""
        var selectedMatch: Int? = null
    }
}


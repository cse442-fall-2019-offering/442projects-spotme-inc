package edu.buffalo.cse.cse442f19.spotme

class Globals {
    companion object {
        @JvmStatic
        var currentUser: User? = null

        var acceptedUsersOneWay = arrayListOf<User.ScoredUser>()
        var currentAcceptedUsers = arrayListOf<User.ScoredUser>()
        var oustring: String = ""

        var selectedMatch: Int? = null
        const val ENDPOINT_BASE = "http://10.84.30.28:5000"//"https://api.spot-me.xyz"
    }
}


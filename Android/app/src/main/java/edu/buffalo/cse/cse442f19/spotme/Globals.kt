package edu.buffalo.cse.cse442f19.spotme

class Globals {
    companion object {
        @JvmStatic
        var currentUser: User? = null
        var acceptedUsersOneWay = arrayListOf<User>()
        var currentAcceptedUsers = arrayListOf<User>()
        var selectedMatch: Int? = null
        const val ENDPOINT_BASE = "https://api.spot-me.xyz"
    }
}


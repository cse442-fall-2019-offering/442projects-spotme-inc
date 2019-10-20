package edu.buffalo.cse.cse442f19.spotme

import org.json.JSONObject

class User {
    var dob: String = ""
    var gender: Int = 0
    var id: Int = 1
    var lat: Double = 0.0
    var level: Int = 1
    var lon: Double = 0.0
    var name: String = ""
    var username: String = ""
    var weight: Double = 300.0
    var partner_gender: Int = 0
    var partner_level: Int = 0
    var radius: Int = 10

    fun getProfileFields(): Map<String, String> {

        return mapOf<String, String>(
            Pair<String, String>("Name", "$name"),
            Pair<String, String>("Username", "$username"),
            Pair<String, String>("Gender", "$gender"),
            Pair<String, String>("Birthday", "$dob"),
            Pair<String, String>("Level", "$level"),
            Pair<String, String>("Weight", "$weight"),
            Pair<String, String>("Lat", "$lat"),
            Pair<String, String>("Lon", "$lon")

        );
//        return arrayOf<String>("$name", "$username", "$dob", "$gender", "$level", "$weight", "$lat", "$lon")
    }

    companion object {
        @JvmStatic
        fun fromJson(jsonObject: JSONObject): User {
            val u = User()

            u.id = jsonObject.optInt("id")
            u.username = jsonObject.optString("username")
            u.name = jsonObject.optString("name")
            u.dob = jsonObject.getString("dob")
            u.gender = jsonObject.optInt("gender")
            u.lat = jsonObject.optDouble("lat")
            u.lon = jsonObject.optDouble("lon")
            u.level = jsonObject.optInt("level")
            u.partner_gender = jsonObject.optInt("partner_gender")
            u.partner_level = jsonObject.optInt("partner_level")
            u.radius = jsonObject.optInt("radius")
            u.weight = jsonObject.optDouble("weight")

            return u
        }
    }

    fun toJson(): JSONObject {
        var o = JSONObject()

        o.put("id", this.id)
        o.put("username", this.username)
        o.put("name", this.name)
        o.put("dob", this.dob)
        o.put("gender", this.gender)
        o.put("lat", this.lat)
        o.put("lon", this.lon)
        o.put("level", this.level)
        o.put("partner_gender", this.partner_gender)
        o.put("partner_level", this.partner_level)
        o.put("radius", this.radius)
        o.put("weight", this.weight)

        return o
    }
}
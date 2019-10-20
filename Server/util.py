import math

def match_score(user1, user2):
    DIST_FACTOR = 1

    score = 0

    if user1.lat is not None and user1.lon is not None and \
            user2.lat is not None and user2.lon is not None:
        distance = haversine(user1.lat, user1.lon, user2.lat, user2.lon)
        distance = max(distance / 1000, 1)
        score += DIST_FACTOR / distance**2

    return score

# Haversine method for finding distance of two points on a sphere.
# Source: https://janakiev.com/blog/gps-points-distance-python/
# Latitute and longitude must be in degrees.
# Returns distance in meters.
def haversine(lat1, lon1, lat2, lon2):
    R = 6372800  # Earth radius in meters

    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi       = math.radians(lat2 - lat1)
    dlambda    = math.radians(lon2 - lon1)

    a = math.sin(dphi / 2)**2 + \
        math.cos(phi1) * math.cos(phi2) * math.sin(dlambda / 2)**2

    return 2 * R * math.atan2(math.sqrt(a), math.sqrt(1 - a))

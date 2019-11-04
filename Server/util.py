import math

def match_score(user1, user2):
    DIST_FACTOR = 1
    FITNESS_FACTOR = 2

    cost = 0

    # Distance
    if user1.lat is not None and user1.lon is not None and \
            user2.lat is not None and user2.lon is not None:
        distance = haversine(user1.lat, user1.lon, user2.lat, user2.lon)
        distance = max(distance / 1000, 1)
        cost += DIST_FACTOR * distance**2

    # Partner fitness level preference
    if user1.partner_level is not None and user2.level is not None and user1.partner_level != 3:
        cost += FITNESS_FACTOR * (user1.partner_level - user2.level)**2

    if user2.partner_level is not None and user1.level is not None and user2.partner_level != 3:
        cost += FITNESS_FACTOR * (user2.partner_level - user1.level)**2

    return 1 / cost

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

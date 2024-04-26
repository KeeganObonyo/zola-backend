Google Places API And Implications on Zola:

Find Place API to retrieve only the place_id for the next request if we leave the params free:
https://maps.googleapis.com/maps/api/place/findplacefromtext/json
  ?fields=formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry
  &input=Museum%20of%20Contemporary%20Art%20Australia
  &inputtype=textquery
  &key=YOUR_API_KEY
Find Place API Response:

````{
    "candidates": [
        {
            "place_id": "ChIJ68aBlEKuEmsRHUA9oME5Zh0"
        }
    ],
    "status": "OK"
}```

Find Place Detail API Request: we can add different paramas to return. Simple for now.

https://maps.googleapis.com/maps/api/place/details/json
  ?fields=name%2Crating%2Cformatted_phone_number 
  &place_id=ChIJ68aBlEKuEmsRHUA9oME5Zh0
  &key=YOUR_API_KEY

Find Place Detail API Response:

````{
    "html_attributions": [],
    "result": {
        "formatted_phone_number": "(02) 9245 2400",
        "name": "Museum of Contemporary Art Australia",
        "rating": 4.4,
        "reviews": [
            {
                "author_name": "Jeanette Newberry",
                "author_url": "https://www.google.com/maps/contrib/110365428313853037709/reviews",
                "language": "en",
                "original_language": "en",
                "profile_photo_url": "https://lh3.googleusercontent.com/a/ACg8ocKBIlzCTn0QvYmfS9OoplLNpsRBGQEwnA4YvEjS6f3Gdm0W7g=s128-c0x00000000-cc-rp-mo",
                "rating": 5,
                "relative_time_description": "in the last week",
                "text": "I have visited the MCA over many years and always enjoyed the exhibitions.  It's relaxing and informative in a creative environment.",
                "time": 1714122140,
                "translated": false
            },
            {
                "author_name": "Md. Yaqub Ali",
                "author_url": "https://www.google.com/maps/contrib/118373787968876721608/reviews",
                "language": "en",
                "original_language": "en",
                "profile_photo_url": "https://lh3.googleusercontent.com/a-/ALV-UjX2g_uDfGFiLPn4l2Q77MsTNUq3q-1UdpPy3ABHTwGbS6scBDXYCg=s128-c0x00000000-cc-rp-mo-ba6",
                "rating": 5,
                "relative_time_description": "in the last week",
                "text": "It’s nice place to explore the colour of life. Take your time to watch each painting and audiovisual. You will be amazed. The cafe on the top floor has the best view of Harbour bridge and Opera House. Take some food and pass some memorable time with your friends and family. The shop on level 1 has various types of gifts.",
                "time": 1714118662,
                "translated": false
            },
            {
                "author_name": "Gabriele Krietemeyer",
                "author_url": "https://www.google.com/maps/contrib/103702604211975492296/reviews",
                "language": "de",
                "original_language": "de",
                "profile_photo_url": "https://lh3.googleusercontent.com/a/ACg8ocJ0W9Jb79J91ELMNG-k2jf465mtCxB8VW3-1d1opY8Jm-8_JQ=s128-c0x00000000-cc-rp-mo-ba5",
                "rating": 4,
                "relative_time_description": "in the last week",
                "text": "Wir hatten uns nicht so viel Zeit genommen, wie dieses Museum verdient hat. Viele Video- Installationen, vieles, das zum Nachdenken anregt, aber auch viel für Kinder.\nUnd ein schönes Rooftop Café! Gerne hingehen!!",
                "time": 1714083377,
                "translated": false
            },
            {
                "author_name": "Hugh",
                "author_url": "https://www.google.com/maps/contrib/112359037941093879882/reviews",
                "profile_photo_url": "https://lh3.googleusercontent.com/a-/ALV-UjViIShzdlQ_1JUbKmqIed0iJSu5Bv6lgt0FKc8Gmv5GBACNics=s128-c0x00000000-cc-rp-mo-ba3",
                "rating": 5,
                "relative_time_description": "in the last week",
                "text": "",
                "time": 1713999924,
                "translated": false
            },
            {
                "author_name": "Rob Gibson",
                "author_url": "https://www.google.com/maps/contrib/110243788243622438190/reviews",
                "profile_photo_url": "https://lh3.googleusercontent.com/a-/ALV-UjWedFC97HIMWaMTEgU7VLZnK1beqmiQhk4PrfkOGKNk6V92knVG=s128-c0x00000000-cc-rp-mo-ba2",
                "rating": 3,
                "relative_time_description": "in the last week",
                "text": "",
                "time": 1713996558,
                "translated": false
            }
        ]
    },
    "status": "OK"
}````

Specify reviews_no_translations=true to disable translation of reviews; specify reviews_no_translations=false to enable translation of reviews.
Reviews are returned in their original language.

########language#######
The language in which to return results.
See the list of supported languages.
Google often updates the supported languages, so this list may not be exhaustive.
If language is not supplied, the API attempts to use the preferred language as specified in the Accept-Language header.
The API does its best to provide a street address that is readable for both the user and locals.
To achieve that goal, it returns street addresses in the local language, transliterated to a script readable by the user if necessary, observing the preferred language. 
All other addresses are returned in the preferred language. Address components are all returned in the same language, which is chosen from the first component.
If a name is not available in the preferred language, the API uses the closest match.
The preferred language has a small influence on the set of results that the API chooses to return, and the order in which they are returned.
The geocoder interprets abbreviations differently depending on language, such as the abbreviations for street types, or synonyms that may be valid in one language but not in another.
For example, utca and tér are synonyms for street in Hungarian.
#######################

####reviews_no_translations###
If omitted, or passed with no value, translation of reviews is enabled. 
If the language parameter was specified in the request, use the specified language as the preferred language for translation.
If language is omitted, the API attempts to use the Accept-Language header as the preferred language.
##############################

reviews_sort#####################
The sorting method to use when returning reviews. Can be set to most_relevant (default) or newest.
#######################################

###################################
For most_relevant (default), reviews are sorted by relevance; the service will bias the results to return reviews originally written in the preferred language.
For newest, reviews are sorted in chronological order; the preferred language does not affect the sort order.
Google recommends that you display how the reviews are being sorted to the end user.
##############################################

################sessiontoken#################
A random string which identifies an autocomplete session for billing purposes.

The session begins when the user starts typing a query, and concludes when they select a place and a call to Place Details is made. Each session can have multiple queries, followed by one place selection. The API key(s) used for each request within a session must belong to the same Google Cloud Console project. Once a session has concluded, the token is no longer valid; your app must generate a fresh token for each session. If the sessiontoken parameter is omitted, or if you reuse a session token, the session is charged as if no session token was provided (each request is billed separately).

We recommend the following guidelines:

Use session tokens for all autocomplete sessions.
Generate a fresh token for each session. Using a version 4 UUID is recommended.
Ensure that the API key(s) used for all Place Autocomplete and Place Details requests within a session belong to the same Cloud Console project.
Be sure to pass a unique session token for each new session. Using the same token for more than one session will result in each request being billed individually.
################################
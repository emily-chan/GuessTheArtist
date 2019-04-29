# twitterexaqmple.py
# Demonstrates connecting to the twitter API and accessing the twitter stream
# Author: Jin Jung
# Email: jijung@chapman.edu
# Course: CPSC 353
# Assignment: PA01 Sentiment Analysis
# Version 1.2

# Demonstrates connecting to the twitter API and accessing the twitter stream

import twitter
import json
import sys

# XXX: Go to http://dev.twitter.com/apps/new to create an app and get values
# for these credentials, which you'll need to provide in place of these
# empty string values that are defined as placeholders.
# See https://dev.twitter.com/docs/auth/oauth for more information
# on Twitter's OAuth implementation.

CONSUMER_KEY = 'V7eaCSg7jMIU3NRTXJK0nKVaL'
CONSUMER_SECRET = '8PnkFbV5JLsFR1UyAv9YYskjq2dJXtJUHMA6DCM6Ql96SziA1g'
OAUTH_TOKEN = '1092464339833901057-V9LEVQnHw26XgJZ106WiT2BGMX2SyR'
OAUTH_TOKEN_SECRET = '8d5REExix2JasoDaPPIZ6qa1n6qmOFDPxwgsDpwAZy4vR'
auth = twitter.oauth.OAuth(OAUTH_TOKEN, OAUTH_TOKEN_SECRET,
                           CONSUMER_KEY, CONSUMER_SECRET)

twitter_api = twitter.Twitter(auth=auth)

WORLD_WOE_ID = 1
US_WOE_ID = 23424977


world_trends = twitter_api.trends.place(_id=WORLD_WOE_ID)
us_trends = twitter_api.trends.place(_id=US_WOE_ID)

world_trends_set = set([trend['name']
                        for trend in world_trends[0]['trends']])

us_trends_set = set([trend['name']
                     for trend in us_trends[0]['trends']])

common_trends = world_trends_set.intersection(us_trends_set)


q = sys.argv[1];

# print q
# raw_input("Press Enter to continue")

count = 1000

# See https://dev.twitter.com/docs/api/1.1/get/search/tweets

search_results = twitter_api.search.tweets(q=q, count=count)

statuses = search_results['statuses']

# Iterate through 5 more batches of results by following the cursor

for _ in range(5):
    try:
        next_results = search_results['search_metadata']['next_results']
    except KeyError, e:  # No more results when next_results doesn't exist
        break

    # Create a dictionary from next_results, which has the following form:
    # ?max_id=313519052523986943&q=NCAA&include_entities=1
    kwargs = dict([kv.split('=') for kv in next_results[1:].split("&")])

    search_results = twitter_api.search.tweets(**kwargs)
    statuses += search_results['statuses']


status_texts = [status['text']
                for status in statuses]

screen_names = [user_mention['screen_name']
                for status in statuses
                for user_mention in status['entities']['user_mentions']]

hashtags = [hashtag['text']
            for status in statuses
            for hashtag in status['entities']['hashtags']]

# Compute a collection of all words from all tweets
words = [w
         for t in status_texts
         for w in t.split()]

# A function for computing lexical diversity
def lexical_diversity(tokens):
    return 1.0*len(set(tokens))/len(tokens)


# A function for computing the average number of words per tweet
def average_words(statuses):
    total_words = sum([len(s.split()) for s in statuses])
    return 1.0*total_words/len(statuses)



# Get the original tweet id for a tweet from its retweeted_status node
# and insert it here in place of the sample value that is provided
# from the text of the book

_retweets = twitter_api.statuses.retweets(id=317127304981667841)


sent_file = open('AFINN-111.txt')

scores = {}  # initialize an empty dictionary
for line in sent_file:
    term, score = line.split("\t")
    # The file is tab-delimited.
    # "\t" means "tab character"
    scores[term] = int(score)  # Convert the score to an integer.

score = 0
for word in words:
    uword = word.encode('utf-8')
    if uword in scores.keys():
        score = score + scores[word]
print float(score)

package chatbotserver;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {

	ConfigurationBuilder builder;
	Configuration configuration;
	TwitterFactory factory;
	twitter4j.Twitter twitter;
	AccessToken accessToken;
	Trends trends;

	private final String CONSUMER_KEY = "xHWk7DirpkWprk1dKnWcRKI9t";
	private final String CONSUMER_SECRET = "9KitWTCFARNES5kLOVYOCOZ2AdOjNzmjG8QIJPiGjyZuvwnaze";

	private final String TOKEN = "871860934398140417-6T23yDaPbwqtD5WZyn2xjWDim8VONgd";
	private final String TOKEN_SECRET = "ixVOf5hrDLWltAnGtIun6t2lXFQFgzkSvBVC9e4YVS4Cw";

	public Twitter() {
		try {
			builder = new ConfigurationBuilder();

			builder.setOAuthConsumerKey(CONSUMER_KEY);
			builder.setOAuthConsumerSecret(CONSUMER_SECRET);

			configuration = builder.build();

			factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			accessToken = new AccessToken(TOKEN, TOKEN_SECRET);

			twitter.setOAuthAccessToken(accessToken);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtém as 10 top trends mundiais.
	 * 
	 * @return
	 */
	public String getTrends() {

		try {
			/**
			 * Trends são por lugares. O parâmetro 1 indica que quero as
			 * worlwide trends.
			 */
			trends = twitter.getPlaceTrends(1);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return this.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		int count = 0;
		for (Trend trend : trends.getTrends()) {
			if (count < 10) {
				sb.append(trend.getName() + "\n");
				count++;
			}
		}
		return sb.toString();
	}
}

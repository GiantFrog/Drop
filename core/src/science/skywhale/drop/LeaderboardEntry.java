package science.skywhale.drop;

public class LeaderboardEntry
{
	private String name;
	private int score;

	public LeaderboardEntry (String name, int score)
	{
		this.name = name;
		this.score = score;
	}
	public LeaderboardEntry()
	{}

	public String getName() {
		return name;
	}
	public int getScore() {
		return score;
	}
}

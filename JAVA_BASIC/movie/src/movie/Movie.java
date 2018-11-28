package movie;

public abstract class Movie{	 
	private String title;
	private String[] actors;
	private String director;

	public Movie(String initTitle, String[] initActors, String initDirector){
		this.title = initTitle;
		this.actors = initActors;
		this.director = initDirector;
	}

	public String getTitle(){ return this.title; }
	public String[] getActors(){ return this.actors; }
	public String getDirector(){ return this.director; }
	public String toString(){ return this.title; }
}
 

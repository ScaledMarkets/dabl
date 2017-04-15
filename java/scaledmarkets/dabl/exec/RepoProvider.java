package scaledmarkets.dabl.exec;

public interface RepoProvider {
	Repo getRepo(String scheme, path, userid, password) throws Exception;
}

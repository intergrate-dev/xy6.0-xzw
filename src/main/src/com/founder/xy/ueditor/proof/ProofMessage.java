package com.founder.xy.ueditor.proof;

public class ProofMessage {
	public int ErrorCount;
	public String [] ErrorWords;
	public String [] ErrorLevels;
	public String [] SuggestWords;
	public String LeaderSortErrors;
	public String LeadErrorCount;
	public String [] SourceSentence;
	public String [] ErrorPoses;
	public int getErrorCount() {
		return ErrorCount;
	}
	public void setErrorCount(int errorCount) {
		ErrorCount = errorCount;
	}
	public String[] getErrorWords() {
		return ErrorWords;
	}
	public void setErrorWords(String[] errorWords) {
		ErrorWords = errorWords;
	}
	public String[] getErrorLevels() {
		return ErrorLevels;
	}
	public void setErrorLevels(String[] errorLevels) {
		ErrorLevels = errorLevels;
	}
	public String[] getSuggestWords() {
		return SuggestWords;
	}
	public void setSuggestWords(String[] suggestWords) {
		SuggestWords = suggestWords;
	}
	public String getLeaderSortErrors() {
		return LeaderSortErrors;
	}
	public void setLeaderSortErrors(String leaderSortErrors) {
		LeaderSortErrors = leaderSortErrors;
	}
	public String getLeadErrorCount() {
		return LeadErrorCount;
	}
	public void setLeadErrorCount(String leadErrorCount) {
		LeadErrorCount = leadErrorCount;
	}
	public String[] getSourceSentence() {
		return SourceSentence;
	}
	public void setSourceSentence(String[] sourceSentence) {
		SourceSentence = sourceSentence;
	}
	public String[] getErrorPoses() {
		return ErrorPoses;
	}
	public void setErrorPoses(String[] errorPoses) {
		ErrorPoses = errorPoses;
	}
}

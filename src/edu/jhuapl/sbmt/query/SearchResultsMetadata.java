package edu.jhuapl.sbmt.query;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import edu.jhuapl.saavtk.metadata.api.Key;
import edu.jhuapl.saavtk.metadata.api.Version;
import edu.jhuapl.saavtk.metadata.impl.FixedMetadata;
import edu.jhuapl.saavtk.metadata.impl.SettableMetadata;

public class SearchResultsMetadata implements SearchMetadata
{
    private static final Version FIXEDLISTRESULTS_DATA_VERSION = Version.of(1, 0);
    /*
     * This Pattern will match on either quoted text or text between commas,
     * including whitespace, and accounting for beginning and end of line. Cribbed
     * from a Stacktrace post.
     */
    private static final Pattern CSV_PATTERN = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");

    // Metadata keys.
    static final Key<String> NAME = Key.of("Results Name");
    static final Key<List<List<String>>> RESULT_LIST = Key.of("Results list");

    FixedMetadata searchMetadata;


    public static SearchResultsMetadata of(String name, List<List<String>> resultList)
    {
        FixedMetadata metadata = createMetadata(name, resultList);
        return new SearchResultsMetadata(metadata);
    }

    private static FixedMetadata createMetadata(String name, List<List<String>> resultList)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(resultList);

        SettableMetadata metadata = SettableMetadata.of(FIXEDLISTRESULTS_DATA_VERSION);
        metadata.put(SearchResultsMetadata.NAME, name);

        metadata.put(SearchResultsMetadata.RESULT_LIST, resultList);
        return FixedMetadata.of(metadata);
    }

    private final FixedMetadata metadata;
    private boolean loadFailed;

    protected SearchResultsMetadata(FixedMetadata metadata)
    {
        this.metadata = metadata;
        this.loadFailed = false;
    }

    public FixedMetadata getMetadata()
    {
        return metadata;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (loadFailed ? 1231 : 1237);
        result = prime * result
                + ((metadata == null) ? 0 : metadata.hashCode());
        result = prime * result
                + ((searchMetadata == null) ? 0 : searchMetadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchResultsMetadata other = (SearchResultsMetadata) obj;
        if (loadFailed != other.loadFailed)
            return false;
        if (metadata == null)
        {
            if (other.metadata != null)
                return false;
        }
        else if (!metadata.equals(other.metadata))
            return false;
        if (searchMetadata == null)
        {
            if (other.searchMetadata != null)
                return false;
        }
        else if (!searchMetadata.equals(other.searchMetadata))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "SearchResultsMetadata [searchMetadata=" + searchMetadata
                + ", metadata=" + metadata + ", loadFailed=" + loadFailed + "]";
    }

    public SearchResultsMetadata copy()
    {
        return new SearchResultsMetadata(getMetadata().copy());
    }

    public List<List<String>> getResultlist()
    {
        return getMetadata().get(RESULT_LIST);
    }
}

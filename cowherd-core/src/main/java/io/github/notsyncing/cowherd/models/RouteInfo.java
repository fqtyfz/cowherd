package io.github.notsyncing.cowherd.models;

import io.github.notsyncing.cowherd.commons.RouteType;
import io.github.notsyncing.cowherd.utils.StringUtils;

import java.util.regex.Pattern;

public class RouteInfo implements Comparable<RouteInfo>
{
    private String domain;
    private String path;
    private Pattern domainPattern;
    private Pattern pathPattern;
    private boolean entry;
    private RouteType type = RouteType.Http;

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Pattern getDomainPattern()
    {
        if (domainPattern == null) {
            if (StringUtils.isEmpty(domain)) {
                return null;
            }

            domainPattern = Pattern.compile(domain);
        }

        return domainPattern;
    }

    public Pattern getPathPattern()
    {
        if (pathPattern == null) {
            if (StringUtils.isEmpty(path)) {
                return null;
            }

            pathPattern = Pattern.compile(path);
        }

        return pathPattern;
    }

    public boolean isEntry()
    {
        return entry;
    }

    public void setEntry(boolean entry)
    {
        this.entry = entry;
    }

    public RouteType getType()
    {
        return type;
    }

    public void setType(RouteType type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof RouteInfo)) return false;

        RouteInfo routeInfo = (RouteInfo) o;

        return getDomain().equals(routeInfo.getDomain()) && getPath().equals(routeInfo.getPath());

    }

    @Override
    public int hashCode()
    {
        int result = getDomain() == null ? 0 : getDomain().hashCode();
        result = 31 * result + getPath().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        String s = getPath();

        if ((getDomain() != null) && (!getDomain().isEmpty())) {
            s = getDomain() + "@" + s;
        }

        return s;
    }

    @Override
    public int compareTo(RouteInfo o)
    {
        String a = o.toString();
        String b = toString();

        if (a.length() == b.length()) {
            return o.toString().compareTo(toString());
        } else {
            return a.length() - b.length();
        }
    }
}

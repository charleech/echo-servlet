package app.github.charleech.echo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * This is a concrete implementing class which provides the feature as an
 * {@code Echo Servlet}.
 * </p>
 *
 * @author charlee.ch
 * @version 1.0.0
 * @see HttpServlet
 */
@WebServlet(
    name        = "EchoServlet",
    urlPatterns = {
        "/echo/*"
    }
)
public class Echo extends HttpServlet {

    /**
     * This is a default serial version {@code UID} as {@value}.
     *
     * @since 1.0.0
     */
    private static final long serialVersionUID = 1L;

    /**
     * This is a constant which represents the length.
     *
     * @since 1.0.0
     */
    private static final int LENGTH = 80;

    /**
     * This is a constant which represents the padding.
     *
     * @since 1.0.0
     */
    private static final String PAD = "=";

    /**
     * This is a constant which represents the new line.
     *
     * @since 1.0.0
     */
    private static final String NEW_LINE = "\r\n";


    @Override
    protected void doGet(final HttpServletRequest  req,
                         final HttpServletResponse res)
                   throws ServletException,
                          IOException {
        this.perform(req,
                     res,
                     false);
    }

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse res)
                   throws ServletException,
                          IOException {
        this.perform(req,
                     res,
                     true);
    }

    @Override
    protected void doPut(final HttpServletRequest  req,
                         final HttpServletResponse res)
                   throws ServletException,
                          IOException {
        this.perform(req,
                     res,
                     true);
    }

    @Override
    protected void doDelete(final HttpServletRequest  req,
                            final HttpServletResponse res)
                   throws ServletException,
                          IOException {
        this.perform(req,
                     res,
                     false);
    }

    /**
     * Perform.
     *
     * @param req
     *            The request
     * @param res
     *            The response
     * @param isReadBody
     *            The flag which identifies if to read from body or to get from
     *            parameter
     * @throws ServletException
     *             If there is any error
     * @throws IOException
     *             If there is any error
     * @since 1.0.0
     */
    protected void perform(final HttpServletRequest  req,
                           final HttpServletResponse res,
                           final boolean             isReadBody)
                   throws ServletException,
                          IOException {
        StringBuilder bldr = null;
        try {
            bldr = new StringBuilder();
            bldr.append(
                     this.getTextHeader("The HTTP Request Header")
                 ).
                 append(
                     this.mapToString(this.getHeaders(req))
                 ).
                 append(
                     this.getTextHeader("The Servlet Information")
                 ).
                 append(
                     this.mapToString(this.getServletInfo(req))
                 ).
                 append(
                     this.getTextHeader("The Client Certificate")
                 ).
                 append(
                     this.mapToString(this.getClientCerts(req))
                 );

            if (isReadBody) {
                bldr.append(
                         this.getTextHeader("The Request Body")
                     ).
                     append(this.getRequestBody(req));
            } else {
                bldr.append(
                    this.getTextHeader("The Request Parameter")
                ).
                append(this.getParameters(req));
            }

            this.manageResponse(
                res,
                "text/plain",
                bldr.toString().getBytes(StandardCharsets.UTF_8)
            );

        } finally {
            bldr = null;
        }
    }

    /**
     * Get Servlet information.
     *
     * @param req The getting request
     * @return The Servlet information
     * @since 1.0.0
     */
    protected Map<String, String> getServletInfo(final HttpServletRequest req) {
        Map<String, String> infos = null;
        try {

            infos = new TreeMap<>();

            infos.put("local-addr",
                      req.getLocalAddr());
            infos.put("local-name",
                      req.getLocalName());
            infos.put("local-port",
                      String.valueOf(req.getLocalPort()));

            infos.put("remote-addr",
                      req.getRemoteAddr());
            infos.put("remote-host",
                      req.getRemoteHost());
            infos.put("remote-user",
                      req.getRemoteUser());
            infos.put("remote-port",
                      String.valueOf(req.getRemotePort()));

            infos.put("servlet-contextpath",
                      req.getContextPath());
            infos.put("servlet-pathinfo",
                      req.getPathInfo());
            infos.put("servlet-pathtranslated",
                      req.getPathTranslated());
            infos.put("servlet-protocol",
                      req.getProtocol());
            infos.put("servlet-scheme",
                      req.getScheme());
            infos.put("servlet-servername",
                      req.getServerName());

            return infos;
        } finally {
            infos = null;
        }
    }

    /**
     * Get the request header information.
     *
     * @param req
     *            The getting request
     * @return The request header information
     * @since 1.0.0
     */
    protected Map<String, String> getHeaders(final HttpServletRequest req) {
        Map<String, String> infos = null;
        try {

            infos = Collections.
                        list(req.getHeaderNames()).
                        stream().
                        collect(
                            Collectors.toMap(
                                e -> e,
                                req::getHeader,
                                (oldval, newval) -> newval,
                                TreeMap::new
                            )

                        );

            return infos;
        } finally {
            infos = null;
        }
    }

    /**
     * Get request body.
     *
     * @param req The getting request
     * @return The request body
     * @since 1.0.0
     */
    protected String getRequestBody(final HttpServletRequest req) {
        try {
            return IOUtils.toString(
                       req.getInputStream(),
                       StandardCharsets.UTF_8
                   )
                   + Echo.NEW_LINE;
        } catch (final IOException e) {
            return "Cannot read request body";
        }
    }

    /**
     * Get the request parameter information.
     *
     * @param req
     *            The getting request
     * @return The request parameter information
     * @since 1.0.0
     */
    protected Map<String, String> getParameters(final HttpServletRequest req) {
        Map<String, String> infos = null;
        try {

            infos = req.getParameterMap().
                        entrySet().
                        stream().
                        collect(
                            Collectors.toMap(
                                Map.Entry::getKey,
                                e -> StringUtils.join(
                                         e.getValue(),
                                         ","
                                     ),
                                (oldval, newval) -> newval,
                                TreeMap::new
                            )
                       );

            return infos;
        } finally {
            infos = null;
        }
    }

    /**
     * Get the request client certificate information.
     *
     * @param req
     *            The getting request
     * @return The request client certificate information
     * @since 1.0.0
     */
    protected Map<String, String> getClientCerts(final HttpServletRequest req) {
        Map<String, String> infos = null;
        Object              raw   = null;
        try {
            infos  = new TreeMap<>();

            raw    = req.getAttribute("javax.servlet.request.X509Certificate");

            if (Objects.isNull(raw)) {
                infos.put("No client certificate", "");
                return infos;
            }

            /*
             * Local variable chains defined in an enclosing scope must be final
             * or effectively final
             */
            final X509Certificate[] chains = X509Certificate[].class.cast(raw);


            infos = IntStream.
                        range(0, chains.length).
                        boxed().
                        collect(
                            Collectors.toMap(
                                i -> "level-" + i,
                                i -> chains[i].getSubjectDN()
                                     + "[" + chains[i].getIssuerDN() + "]",
                                (oldval, newval) -> newval,
                                TreeMap::new
                            )
                        );

            return infos;
        } finally {
            infos = null;
            raw   = null;
        }
    }

    /**
     * Manage the response.
     *
     * @param res
     *            The response
     * @param contentType
     *            The returning content type
     * @param data
     *            The returning data
     * @throws IOException
     *             If there is any error
     * @since 1.00
     */
    protected void manageResponse(final HttpServletResponse res,
                                  final String              contentType,
                                  final byte[]              data)
                throws IOException {

        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(contentType);
        res.setContentLength(data.length);

        /*
         * Set standard HTTP/1.1 no-cache headers.
         */
        res.setHeader("Cache-Control",
                      "private, no-store, no-cache, must-revalidate");

        /*
         * Set standard HTTP/1.0 no-cache header.
         */
        res.setHeader("Pragma", "no-cache");

        res.setHeader("Expires", "0");

        res.getOutputStream().write(data);

    }

    /**
     * Convert map to string delimited by new line.
     *
     * @param srcs
     *            The converting source
     * @return The converted
     * @since 1.0.0
     */
    protected String mapToString(final Map<String, String> srcs) {
        SortedMap<String, String> sorted = null;
        String                    data   = null;
        try {
            sorted = new TreeMap<>(srcs);

            data   = sorted.
                         entrySet().
                         stream().
                         map(
                             entry -> entry.getKey() + " : " + entry.getValue()
                         ).
                         collect(
                             Collectors.joining("\r\n")
                         );
            data   = data
                    + Echo.NEW_LINE;
            return data;
        } finally {
            sorted = null;
            data   = null;
        }
    }

    /**
     * Get header with padding.
     *
     * @param header
     *            the header
     * @return The padded
     * @since 1.0.0
     */
    protected String getTextHeader(final String header) {
        return StringUtils.center(
                   header,
                   Echo.LENGTH,
                   Echo.PAD
                )
                + Echo.NEW_LINE;
    }
}

package org.barcelonamedia.uima.consumer.solrConsumer.posting;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.URL;
import java.net.ProtocolException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * A simple utility class for posting raw updates to a Solr server, has a main
 * method so it can be run on the command line.
 */
public class SimplePostWriter extends java.io.Writer {

    /** The Constant BUFF_SIZE. */
    private static final int BUFF_SIZE = 1024;

    /** The Constant DEFAULT_POST_URL. */
    public static final String DEFAULT_POST_URL = "http://localhost:8180/solr/update";

    /** The Constant POST_ENCODING. */
    public static final String POST_ENCODING = "UTF-8";

    /** The Constant VERSION_OF_THIS_TOOL. */
    public static final String VERSION_OF_THIS_TOOL = "1.0";

    /** The solr url. */
    protected URL solrUrl;

    /** The writer. */
    private OutputStreamWriter writer;

    /** The out. */
    private OutputStream out;

    /** The urlc. */
    private HttpURLConnection urlc;

    /** The output. */
    private StringWriter output;

    /**
     * The Class PostException.
     */
    private class PostException extends RuntimeException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new post exception.
         * 
         * @param reason
         *            the reason
         * @param cause
         *            the cause
         */
        PostException(final String reason, final Throwable cause) {
            super(reason + " (POST URL=" + solrUrl + ")", cause);
        }
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        info("version " + VERSION_OF_THIS_TOOL);

        if (args.length < 2) {
            fatal("This command requires at least two arguments:\n"
                    + "The destination url and the names of "
                    + "one or more XML files to POST to Solr."
                    + "\n\texample: " + DEFAULT_POST_URL
                    + " somefile.xml otherfile.xml");
        }

        URL solrUrl = null;
        try {
            solrUrl = new URL(args[0]);
        } catch (MalformedURLException e) {
            fatal("First argument is not a valid URL: " + args[0]);
        }

        try {
            int posted = 0;
            SimplePostWriter t;
            for (int i = 1; i < args.length; i++) {
                t = new SimplePostWriter(solrUrl);
                info("POSTing to " + solrUrl + "..");
                posted++;
                FileReader reader = new FileReader(args[i]);
                t.postFile(reader);
                t.close();
                t = null;
            }
            if (posted > 0) {
                info("COMMITting Solr index changes..");
                t = new SimplePostWriter(solrUrl);
                t.writeCommit();
                t.close();
                t = null;
            }
            info(posted + " files POSTed to " + solrUrl);

        } catch (IOException ioe) {
            fatal("Unexpected IOException " + ioe);
        }
    }

    /**
     * Check what Solr replied to a POST, and complain if it's not what we
     * expected. TODO: parse the response and check it XMLwise, here we just
     * check it as an unparsed String
     * 
     * @param actual
     *            the actual
     * @param expected
     *            the expected
     */
    static void warnIfNotExpectedResponse(final String actual,
            final String expected) {
        if (!actual.equals(expected)) {
            warn("Unexpected response from Solr: '" + actual + "', expected '"
                    + expected + "'");
        }
    }

    /**
     * Warn.
     * 
     * @param msg
     *            the msg
     */
    static void warn(final String msg) {
        System.err.println("SimplePostTool: WARNING: " + msg);
    }

    /**
     * Info.
     * 
     * @param msg
     *            the msg
     */
    static void info(final String msg) {
        System.out.println("SimplePostTool: " + msg);
    }

    /**
     * Fatal.
     * 
     * @param msg
     *            the msg
     */
    static void fatal(final String msg) {
        System.err.println("SimplePostTool: FATAL: " + msg);
        System.exit(1);
    }

    /**
     * Does a simple commit operation.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public final void writeCommit() throws IOException {
        write("<commit/>");
    }

    /**
     * Does a performs a transfer of a whole file.
     * 
     * @param data
     *            the data
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public final void postFile(final FileReader data) throws IOException {
        pipe(data, writer);
    }

    /**
     * Reads data from the data reader and posts it to solr, writes to the
     * response to output.
     */

    /**
     * inits the post command.
     */
    private void initPost() {

        try {
            output = new StringWriter();
            urlc = (HttpURLConnection) solrUrl.openConnection();
            try {
                urlc.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new PostException(
                        "Shouldn't happen: HttpURLConnection doesn't support POST??",
                        e);
            }
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            urlc.setRequestProperty("Content-type", "text/xml; charset="
                    + POST_ENCODING);

            out = urlc.getOutputStream();

            try {
                writer = new OutputStreamWriter(out, POST_ENCODING);
            } catch (IOException e) {
                throw new PostException("IOException while posting data", e);
            }
        } catch (IOException e) {
            fatal("Connection error (is Solr running at " + solrUrl + " ?): "
                    + e);

        } finally {
            if (urlc != null) {
                urlc.disconnect();
            }
        }
    }

    /**
     * Constructs an instance for posting data to the specified Solr URL (ie:
     * "http://localhost:8983/solr/update").
     * 
     * @param solrUrlParam
     *            the solr url
     */
    public SimplePostWriter(final URL solrUrlParam) {
        this.solrUrl = solrUrlParam;
        warn("Make sure your XML documents are encoded in " + POST_ENCODING
                + ", other encodings are not currently supported");
        this.initPost();
    }

    /**
     * Instantiates a new simple post writer.
     * 
     * @param solrUrlParam
     *            the solr url
     */
    public SimplePostWriter(final String solrUrlParam) {
        try {
            this.solrUrl = new URL(solrUrlParam);
            // warn("Make sure your XML documents are encoded in " +
            // POST_ENCODING
            // + ", other encodings are not currently supported");
            this.initPost();
        } catch (MalformedURLException e) {
            fatal("First argument is not a valid URL: " + solrUrlParam);
        }
    }

    /**
     * Flush.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @see java.io.Writer#flush()
     */
    public final void flush() throws IOException {
        writer.flush();
    }

    /**
     * Close.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @see java.io.Writer#close()
     */
    public final void close() throws IOException {
        // TODO Auto-generated method stub
        flush();
        writer.close();
        InputStream in = urlc.getInputStream();
        try {
            Reader reader = new InputStreamReader(in);
            pipe(reader, output);
            reader.close();
            if (!output.toString().equals("<result status=\"0\"></result>")) {
                System.out.println(output.toString());
            }
            output.close();
        } catch (IOException e) {
            throw new PostException("IOException while reading response", e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (urlc != null) {
                urlc.disconnect();
            }
        }
    }

    /**
     * Write.
     * 
     * @param cbuf
     *            The char buffer.
     * @param off
     *            The offset.
     * @param len
     *            The length.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @see java.io.Writer#write(char[], int, int)
     */
    public final void write(final char[] cbuf, final int off, final int len)
            throws IOException {
        writer.write(cbuf, off, len);
    }

    /**
     * Write.
     * 
     * @param str
     *            the str
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @see java.io.Writer#write(java.lang.String)
     */
    public final void write(final String str) throws IOException {
        writer.write(str);
    }

    /**
     * Pipes everything from the reader to the writer via a buffer.
     * 
     * @param reader
     *            the reader
     * @param writer
     *            the writer
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */

    private static void pipe(final Reader reader, final Writer writer)
            throws IOException {
        char[] buf = new char[BUFF_SIZE];
        int read = 0;
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
    }

}

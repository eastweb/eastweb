package edu.sdstate.eastweb.prototype.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

public final class XmlUtils {
    // Construct a DOMImplementationLS on demand
    private static final LazyCachedReference<DOMImplementationLS, IOException>
    sDomImplementationLS = new LazyCachedReference<DOMImplementationLS, IOException>() {
        @Override
        protected final DOMImplementationLS makeInstance() throws IOException {
            final DOMImplementationRegistry registry;
            try {
                registry = DOMImplementationRegistry.newInstance();
            } catch (IllegalAccessException e) {
                throw new IOException(e);
            } catch (InstantiationException e) {
                throw new IOException(e);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }

            final DOMImplementationLS impl =
                (DOMImplementationLS)registry.getDOMImplementation("XML LS");
            if (impl == null) {
                throw new IOException("No DOM implementation supports the XML and LS features");
            }
            return impl;
        }
    };

    private XmlUtils() {
    }

    /**
     * Returns a new DOM document.
     * @throws IOException thrown if anything goes wrong
     */
    public static final Document newDocument(String rootElementName) throws IOException {
        final DOMImplementation impl = (DOMImplementation)sDomImplementationLS.get();
        synchronized (impl) {
            return impl.createDocument(null, rootElementName, null);
        }
    }

    /**
     * Transforms a DOM document to a file.
     * @param document DOM document to transform
     * @param file Target file
     * @throws IOException thrown if anything goes wrong
     */
    public static final void transformToFile(Document document, File file) throws IOException {
        final DOMImplementationLS impl = sDomImplementationLS.get();
        final LSSerializer serializer;
        final LSOutput output ;
        synchronized (impl) {
            serializer = impl.createLSSerializer();
            output = impl.createLSOutput();
        }
        serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);

        // Open the file
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        Closeable closeable = fileOutputStream;
        try {
            // Write to it
            output.setByteStream(fileOutputStream);
            serializer.write(document, output);
        } finally {
            closeable.close();
        }
    }

    /**
     * Transforms a DOM document to a gzipped file.
     * @param document DOM document to transform
     * @param file Target file
     * @throws IOException thrown if anything goes wrong
     */
    public static final void transformToGzippedFile(Document document, File file) throws IOException {
        final DOMImplementationLS impl = sDomImplementationLS.get();
        final LSSerializer serializer;
        final LSOutput output ;
        synchronized (impl) {
            serializer = impl.createLSSerializer();
            output = impl.createLSOutput();
        }
        serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);

        // Open the file
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        Closeable closeable = fileOutputStream;
        try {
            // Wrap it
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            closeable = gzipOutputStream;

            // Write to it
            output.setByteStream(gzipOutputStream);
            serializer.write(document, output);
        } finally {
            closeable.close();
        }
    }

    /**
     * Parses a stream into a DOM document.
     * @param stream Source stream
     * @throws IOException thrown if anything goes wrong
     */
    public static final Document parse(InputStream stream) throws IOException {
        final DOMImplementationLS impl = sDomImplementationLS.get();
        final LSParser parser;
        final LSInput input;
        synchronized (impl) {
            parser = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
            input = impl.createLSInput();
        }

        // Parse it
        input.setByteStream(stream);
        return parser.parse(input);
    }

    /**
     * Parses a file into a DOM document.
     * @param file Source file
     * @throws IOException thrown if anything goes wrong
     */
    public static final Document parse(File file) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return parse(fileInputStream);
        } finally {
            fileInputStream.close();
        }
    }

    /**
     * Parses a gzipped file into a DOM document.
     * @param file Source file
     * @throws IOException thrown if anything goes wrong
     */
    public static final Document parseGzipped(File file) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        Closeable closeable = fileInputStream;
        try {
            final GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
            closeable = gzipInputStream;

            return parse(gzipInputStream);
        } finally {
            closeable.close();
        }
    }

    public static final Element getElementByTagName(Element parent, String name) throws IOException {
        final NodeList list = parent.getElementsByTagName(name);
        if (list.getLength() == 1) {
            return (Element)list.item(0);
        } else {
            throw new IOException(String.format("Expected one child element named \"%s\"", name));
        }
    }

    public static final List<Element> getChildElements(Element parent) {
        final List<Element> childElements = new ArrayList<Element>();

        final NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                childElements.add((Element)node);
            }
        }

        return childElements;
    }

    public static final Element getChildElement(Element parent) throws IOException {
        final List<Element> childElements = getChildElements(parent);
        if (childElements.size() == 1) {
            return childElements.get(0);
        } else {
            throw new IOException("Expected one child element");
        }
    }
}
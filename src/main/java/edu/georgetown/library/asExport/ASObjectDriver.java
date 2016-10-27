package edu.georgetown.library.asExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ASObjectDriver extends ASDriver {

    OutputStream os;
    public ASObjectDriver(ASParsedCommandLine cmdLine)
            throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
        super(cmdLine);
        File outDir = prop.resetOutputDir();
        File rptDir = new File(outDir, "reports");
        rptDir.mkdirs();
        File f = new File(rptDir, "AS.report.csv");
        os = new FileOutputStream(f);
    }

    public static void main(String[] args) {
        try {
            ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
            asCmdLine.addRepoTypeObject();
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            ASObjectDriver driver = new ASObjectDriver(cmdLine);
            driver.processRequest();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }


    public void processRequest() throws DataException, ClientProtocolException, URISyntaxException, IOException {
        int repo   = cmdLine.getRepositoryId();
        long objid = cmdLine.getObjectId();
        TYPE type  = cmdLine.getType();
        
        JSONObject obj = asConn.getPublishedObject(repo, type, objid);
        if (obj != null) {
            System.out.println(String.format("[%d] %s", objid, obj.toString()));
            ASResource res = new ASResource(obj);
            System.out.println("Title         : "+res.getTitle());
            System.out.println("Date          : "+res.getDate());
            System.out.println("Mod Date      : "+res.getModDate());
            System.out.println("Description   : "+res.getDescription());
            System.out.println("");                    
        } else {
            System.out.println(" *** Unpublished ***\n\n");
        }
        
        Document d;
        try {
            d = asConn.getEADXML(repo, objid);
            saveEAD(d, new File("test.xml"));
            convertEAD(d, new File("test.fmt.xml"), repo, objid);
            dumpEAD(d, os);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
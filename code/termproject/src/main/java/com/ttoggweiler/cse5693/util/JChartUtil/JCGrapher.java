package com.ttoggweiler.cse5693.util.JChartUtil;

import com.ttoggweiler.cse5693.termProjectRunner;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ttoggweiler on 4/23/17.
 */
public class JCGrapher
{
    private static Logger _logger = LoggerFactory.getLogger(JCGrapher.class);

    public static JFreeChart chartTimeSeries(String titile, String xUnits, String yUnits, XYDataset dataSet) throws IOException
    {
        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
                titile,
                xUnits,
                yUnits,
                dataSet,
                true, true, false);
        return timeSeriesChart;
    }

    public static void addIntervalDomainMarkers(XYPlot plot, List<Marker> markers){
        for(Marker m : markers) plot.addDomainMarker(m, Layer.BACKGROUND);
    }

    public static void addValueDomainMarkers(XYPlot plot, List<Marker> markers){
        for(Marker m : markers) plot.addDomainMarker(m, Layer.FOREGROUND);
    }

    public static File saveChart(String path, JFreeChart chart, int width, int height) throws IOException
    {
        File chartFile = new File(path);
        ChartUtilities.saveChartAsPNG(chartFile, chart, width, height);
        return chartFile;
    }

    public static TimeSeries milliTimeSeries(String name, List<Date> milliTimes, List<Integer> values)throws IllegalArgumentException
    {
        if(milliTimes.size() != values.size()) throw new IllegalArgumentException("Lists must be of the same size");

        TimeSeries milliTimeSeries = new TimeSeries(name);

        Iterator<Date> dateItr = milliTimes.iterator();
        Iterator<Integer> valueItr = values.iterator();

        while(dateItr.hasNext()){
            milliTimeSeries.addOrUpdate(new Millisecond(dateItr.next()),(double)valueItr.next());
        }

        return milliTimeSeries;
    }
}
//package org.fc2.util.visualisation;
//
//
//        import com.google.common.collect.Lists;
//        import org.agent.mira.services.auditing.MiraEvent;
//        import org.apache.log4j.Level;
//        import org.apache.log4j.Logger;
//        import org.elasticsearch.action.search.SearchResponse;
//        import org.elasticsearch.index.query.QueryBuilder;
//        import org.fc2.event.FC2Event;
//        import org.jfree.chart.JFreeChart;
//        import org.jfree.chart.LegendItem;
//        import org.jfree.chart.LegendItemCollection;
//        import org.jfree.chart.axis.DateAxis;
//        import org.jfree.chart.axis.NumberAxis;
//        import org.jfree.chart.axis.SymbolAxis;
//        import org.jfree.chart.plot.CombinedDomainXYPlot;
//        import org.jfree.chart.plot.IntervalMarker;
//        import org.jfree.chart.plot.Marker;
//        import org.jfree.chart.plot.ValueMarker;
//        import org.jfree.chart.plot.XYPlot;
//        import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
//        import org.jfree.data.time.TimeSeries;
//        import org.jfree.data.time.TimeSeriesCollection;
//        import org.jfree.data.xy.XYDataset;
//        import org.jfree.ui.RectangleAnchor;
//        import org.jfree.ui.RectangleInsets;
//        import org.jfree.ui.TextAnchor;
//        import org.joda.time.LocalDateTime;
//
//        import java.awt.*;
//        import java.awt.geom.Line2D;
//        import java.io.File;
//        import java.io.IOException;
//        import java.net.UnknownHostException;
//        import java.text.NumberFormat;
//        import java.util.ArrayList;
//        import java.util.Date;
//        import java.util.HashMap;
//        import java.util.Iterator;
//        import java.util.List;
//        import java.util.Map;
//
///**
// * Created by ttogg on 5/10/16.
// */
//public class AuditVisualiser {
//    public static final String ELASTICSEARCH_CLUSTER = "unnamedcluster_visualisation";
//    public static final String ELASTICSEARCH_INDEX_FLOW = "logstash-*";
//    public static final String ELASTICSEARCH_IP = "10.10.22.180";
//    public static final int ELASTICSEARCH_PORT = 9300;
//
//    public static final int CHART_WIDTH = 1500;
//    public static final int CHART_HEIGHT = 750;
//    public static final String FTP_CHART_TITLE = "Moving Target FTP Service";
//    public static final String AXIS_TIME = "Time";
//    public static final String AXIS_SUCC_RATE = "Success Rate";
//
//    public static final String[] orgs = new String[]{"comp1", "comp2", "comp3", "comp4", "comp5"};
//    public static final String[] orgsFQD = new String[]{"Company 1", "Company 2", "Company 3", "Company 4", "Company 5"};
//    public static final LocalDateTime QUERY_START = new LocalDateTime(1463600400000L);
//    public static final LocalDateTime QUERY_END = new LocalDateTime(1463601925000L);
//    public static final int QUERY_SIZE = 100000;
//    public static final BasicStroke LINE_STROKE = new BasicStroke(3);
//
//    private static Logger _logger = Logger.getLogger("");
//
//    public static Querier _data = null;
//
//    public static void main(String[] args) throws Exception
//    {
//        org.apache.log4j.BasicConfigurator.configure();
//        org.apache.log4j.PropertyConfigurator.configure("/Users/ttogg/Documents/repos/mira5/code/src/main/resources/org/agent/mira/providers/logging/Log4JLoggingProvider.properties");
//        try
//        {
//            _data = new Querier(
//                    ELASTICSEARCH_CLUSTER,
//                    ELASTICSEARCH_INDEX_FLOW,
//                    ELASTICSEARCH_IP,
//                    ELASTICSEARCH_PORT);
//        } catch (UnknownHostException e)
//        {
//            _logger.log(Level.ERROR,"Problem connecting to elastic search: \n" + e.getMessage());
//            return;
//        }
//        _logger.log(Level.INFO,"Connected to Elasticsearch");
//        _data.setQuerySize(QUERY_SIZE);
//        visualizeFTPAppos(orgs);
//    }
//
//    private static Date getFirstFailure(List<Map> sensorEvents)
//    {
//        QueryProcessor.TermLists ftpAuditTermLists = new QueryProcessor.TermLists(null, sensorEvents);
//        QueryProcessor.TermLists ftpPayloadTermLists = new QueryProcessor.TermLists(null, ftpAuditTermLists.getTermList(MiraEvent.PAYLOAD));
//        List<Integer> ftpValue = Lists.transform(ftpPayloadTermLists.getTermList("value"), Transform.objToInt);
//        List<Date> dates = Lists.transform(ftpAuditTermLists.getTermList(MiraEvent.TIME), Transform.objToDate);
//        int failureIndex = ftpValue.indexOf(0);
//
//        return (failureIndex >= 0)?new LocalDateTime(dates.get(failureIndex)).minusSeconds(4).toDate():null; //find 0 value for ftp and return same index of dates
//    }
//
//    private static void visualizeFTPAppos(String[] orgs) throws IOException
//    {
//        //Query
//        _logger.log(Level.INFO,"Starting FTP Query");
//        SearchResponse ftpStatusQueryResult = ftpStatusQuery();
//        _logger.log(Level.INFO,"Starting Appos Query");
//        SearchResponse apposEventResult = apposServerQuery();
//        _logger.log(Level.INFO,"Starting Appos Query");
//        SearchResponse suggestionResult = federationSuggestionQuery();
//
//        //data
//        _logger.log(Level.INFO,"Processing query Data");
//        HashMap<Object, List<Map>> sensorEvents = QueryProcessor.mapSourceOnTerm(MiraEvent.EVENT_SUBTYPE, ftpStatusQueryResult.getHits().hits());
//        HashMap<Object, List<Map>> ftpStatusMapByOrg = QueryProcessor.mapSourceOnTerm("org", ftpStatusQueryResult.getHits().hits());
//        HashMap<Object, List<Map>> apposEventMapByOrg = QueryProcessor.mapSourceOnTerm("org", apposEventResult.getHits().hits());
//        HashMap<Object, List<Map>> suggestionMapByOrg = QueryProcessor.mapSourceOnTerm("org", suggestionResult.getHits().hits());
//
//        Date attackDate = null;
//        if(!sensorEvents.isEmpty()) attackDate = getFirstFailure(sensorEvents.get("ftp"));
//
//        //multigraph
//        _logger.log(Level.INFO,"Generating Graphing Objs");
//        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
//        combinedPlot.setGap(15);
//
//        //axes
//        DateAxis domainAxis = new DateAxis(AXIS_TIME);
//        combinedPlot.setDomainAxis(domainAxis);
//
//        //legend
//        LegendItemCollection legend = new LegendItemCollection();
//        LegendItem ftp1 = new LegendItem("FTP1: Vulnerable\t",null,null,null,new Rectangle(10,10),Color.gray,new BasicStroke(0f),Color.gray);
//        legend.add(ftp1);
//
//        LegendItem ftp2 = new LegendItem("FTP2: Not Vulnerable\t",null,null,null,new Rectangle(10,10),Color.lightGray,new BasicStroke(0f),Color.lightGray);
//        legend.add(ftp2);
//
//        //markers
//        BasicStroke attackLine = new BasicStroke(2f,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL,2f,new float[]{3f},0f);
//        LegendItem attackLineLegend = new LegendItem("Attack Started\t",null,null,null,new Line2D.Double(0,0,75,0), Color.black,attackLine,Color.black);
//        legend.add(attackLineLegend);
//
//        BasicStroke sendSugMarker = new BasicStroke(2f,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL,2f,new float[]{20f},0f);
//        LegendItem sendSugLegend = new LegendItem("Suggestion Sent\t",null,null,null,new Line2D.Double(0,0,75,0), Color.black,sendSugMarker,Color.black);
//        //legend.add(sendSugLegend);
//
//        BasicStroke recvSugMarker = new BasicStroke(2f,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL,2f,new float[]{10f},0f);
//        LegendItem recvSugLegend = new LegendItem("Suggestion Received\t",null,null,null,new Line2D.Double(0,0,75,0), Color.black,recvSugMarker,Color.black);
//        legend.add(recvSugLegend);
//        combinedPlot.setFixedLegendItems(legend);
//
//        //Viz
//        for (String org : orgs)
//        {
//            _logger.log(Level.INFO,"Graphing: " + org);
//            //Line
//            StandardXYItemRenderer render = new StandardXYItemRenderer();
//            render.setSeriesPaint(0, Color.black);
//            render.setSeriesStroke(0, LINE_STROKE);
//
//            //Y axis
//            NumberAxis percentAxis = new NumberAxis(getNameForOrg(org));
//            percentAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
//            percentAxis.setRange(0, 1.04);
//
//            //Plot
//            TimeSeries orgFTPSeries = generateFTPTimeSeries(org, ftpStatusMapByOrg.get(org));
//            XYDataset ftpXYData = new TimeSeriesCollection(orgFTPSeries);
//            XYPlot ftpXYPlot = new XYPlot(ftpXYData,domainAxis,percentAxis,render);
//            ftpXYPlot.setFixedLegendItems(legend);
//
//            //markers
//            if(attackDate != null)
//                ftpXYPlot.addDomainMarker(generateAttackMarker("Attack Started",attackDate,Color.black,attackLine));
//
//            if(suggestionMapByOrg.containsKey(org))
//                Grapher.addValueDomainMarkers(ftpXYPlot, generateSuggestionMarkers(suggestionMapByOrg.get(org),sendSugMarker, recvSugMarker));
//
//            Grapher.addIntervalDomainMarkers(ftpXYPlot, generateApposMarkers(apposEventMapByOrg.get(org)));
//
//
//            // Company Chart
//            JFreeChart orgChart = new JFreeChart(FTP_CHART_TITLE,ftpXYPlot);
//            orgChart.setPadding(new RectangleInsets(20f, 20f, 20f, 20f));
//            File chart = Grapher.saveChart(org + ".png", orgChart, CHART_WIDTH, CHART_HEIGHT);
//
//            //add to multiChart
//            combinedPlot.add(ftpXYPlot);
//        }
//
//        _logger.log(Level.INFO,"Graphing: " + "Fed");
//
//        JFreeChart fedChart = new JFreeChart("Federated Moving Target FTP Services", combinedPlot);
//        fedChart.setPadding(new RectangleInsets(20f, 20f, 20f, 20f));
//        File chart = Grapher.saveChart("fed.png", fedChart, CHART_WIDTH, CHART_HEIGHT);
//    }
//
//    private static String getNameForOrg(String org)
//    {
//        switch (org){
//            case "comp1":return "Company 1";
//            case "comp2":return "Company 2";
//            case "comp3":return "Company 3";
//            case "comp4":return "Company 4";
//            case "comp5":return "Company 5";
//            default:return "Independent";
//        }
//    }
//
//    private static List<Marker> generateSuggestionMarkers(List<Map> suggestionEvents, Stroke sendStroke, Stroke recvStroke)
//    {
//        //Get the data
//        List<Marker> suggestionMarkers = new ArrayList<>();
//
//        for(Map<String, Object> suggestionEvent : suggestionEvents){
//            switch ((String)suggestionEvent.get(MiraEvent.EVENT_SUBTYPE)){
//                case FC2Event.SEND_SUGGEST:
//                    //suggestionMarkers.add(new ValueMarker(new Date((Long)suggestionEvent.get(MiraEvent.TIME)).getTime(),Color.black, sendStroke));
//                    break;
//                case FC2Event.RECV_SUGGEST:
//                    suggestionMarkers.add(new ValueMarker(new Date((Long)suggestionEvent.get(MiraEvent.TIME)).getTime(),Color.black, recvStroke));
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        _logger.log(Level.INFO,"Generated "+ suggestionMarkers.size()+ " Suggestion Markers");
//        return suggestionMarkers;
//    }
//
//    private static List<Marker> generateApposMarkers(List<Map> apposEvents)
//    {
//        //Get the data
//        QueryProcessor.TermLists apposAuditTermLists = new QueryProcessor.TermLists(null, apposEvents);
//        QueryProcessor.TermLists apposPayloadTermLists = new QueryProcessor.TermLists(null, apposAuditTermLists.getTermList(MiraEvent.PAYLOAD));
//        List<String> ftpServers = Lists.transform(apposPayloadTermLists.getTermList("name"), Transform.objToString);
//        List<Date> dates = Lists.transform(apposAuditTermLists.getTermList(MiraEvent.TIME), Transform.objToDate);
//
//        if (ftpServers.size() != dates.size()) throw new IllegalArgumentException("Lists are not of the same size");
//
//        List<Marker> apposMarkers = new ArrayList<>();
//        Iterator<String> svrItr = ftpServers.iterator();
//        Iterator<Date> dateItr = dates.iterator();
//
//        //generate makers
//        Date lastEvent = new LocalDateTime(dates.get(0)).minusMinutes(2).toDate(); // fill before the graph
//        Date nextEvent = null;
//        String lastServer = ftpServers.get(0);
//        while (svrItr.hasNext())
//        {
//            nextEvent = dateItr.next();
//            String nextServer = svrItr.next();
//            if(lastServer.equals(nextServer)) continue; //if same server, only draw one marker
//            lastServer = nextServer;
//            Color color = (lastServer.contains("ftp1")) ? Color.gray : Color.lightGray;
//            apposMarkers.add(generateApposMarker(null, lastEvent, nextEvent, color));
//            lastEvent = nextEvent;
//        }
//        Color color = (lastServer.contains("ftp1")) ? Color.gray : Color.lightGray;
//        apposMarkers.add(generateApposMarker(null, lastEvent, new LocalDateTime().plusMinutes(2).toDate(), color)); //fill after the graph
//        return apposMarkers;
//    }
//
//    private static Marker generateApposMarker(String name, Date start, Date end, Color color)
//    {
//        final Marker serverMarker = new IntervalMarker(start.getTime(), end.getTime());
//        serverMarker.setPaint(color);
//        serverMarker.setAlpha(.75f);
//        //label
//        serverMarker.setLabel(name);
//        serverMarker.setLabelPaint(Color.black);
//        serverMarker.setLabelFont(new Font("",0,12));
//        serverMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
//        serverMarker.setLabelTextAnchor(TextAnchor.BOTTOM_CENTER);
//        serverMarker.setOutlineStroke(new BasicStroke(0.00f));
//        //serverMarker.setStroke(new BasicStroke(0));
//        //serverMarker.setOutlineStroke(new BasicStroke(0));
//        return serverMarker;
//    }
//
//    private static Marker generateAttackMarker(String name, Date start, Color color,BasicStroke attackLine)
//    {
//        return new ValueMarker(start.getTime(),color, attackLine);
//    }
//
//    private static SearchResponse ftpStatusQuery()
//    {
//        _data.clearAllConstraints();
//        _data.addTimeRange(QUERY_START, QUERY_END);
//        _data.addTermToFilter("payload.result", null);
//        _data.setTermsToFetch(new String[]{"payload.result", MiraEvent.TIME, "org", "payload.value",MiraEvent.EVENT_SUBTYPE});
//        QueryBuilder FtpStausQuery = _data.getCurrentQuery();
//        SearchResponse response = _data.makeQuery();
//
//        Level lvl = Level.INFO;
//        if(response.getHits().getHits().length != response.getHits().getTotalHits())
//            lvl = Level.WARN;
//
//        _logger.log(lvl,"FTP Query returned " +response.getHits().getHits().length + " of " + response.getHits().getTotalHits());
//        return response;
//    }
//
//    private static SearchResponse apposServerQuery()
//    {
//        _data.clearAllConstraints();
//
//        _data.addTimeRange(QUERY_START, QUERY_END);
//        _data.addTermToFilter(MiraEvent.EVENT_SUBTYPE, "move_started");
//        _data.setTermsToFetch(new String[]{"payload.name", "payload.ip", "org", MiraEvent.TIME});
//        QueryBuilder apposMovesQuery = _data.getCurrentQuery();
//        SearchResponse response = _data.makeQuery();
//
//        Level lvl = Level.INFO;
//        if(response.getHits().getHits().length != response.getHits().getTotalHits())
//            lvl = Level.WARN;
//        _logger.log(lvl,"AppOS Query returned " +response.getHits().getHits().length + " of " + response.getHits().getTotalHits());
//        return response;
//    }
//
//    private static SearchResponse federationSuggestionQuery()
//    {
//        _data.clearAllConstraints();
//
//        _data.addTimeRange(QUERY_START, QUERY_END);
//        _data.addTermToFilter(MiraEvent.EVENT_TYPE, FC2Event.FC2_EVENT_TYPE_KEY);
//        _data.setTermsToFetch(new String[]{MiraEvent.EVENT_SUBTYPE, "org", MiraEvent.TIME});
//        SearchResponse response = _data.makeQuery();
//
//        Level lvl = Level.INFO;
//        if(response.getHits().getHits().length != response.getHits().getTotalHits())
//            lvl = Level.WARN;
//        _logger.log(lvl,"Fed Suggestion Query returned " +response.getHits().getHits().length + " of " + response.getHits().getTotalHits());
//        return response;
//    }
//
//    private static TimeSeries generateFTPTimeSeries(String name, List<Map> ftpEvents)
//    {
//        QueryProcessor.TermLists ftpAuditTermLists = new QueryProcessor.TermLists(null, ftpEvents);
//        QueryProcessor.TermLists ftpPayloadTermLists = new QueryProcessor.TermLists(null, ftpAuditTermLists.getTermList(MiraEvent.PAYLOAD));
//        List<Integer> ftpValue = Lists.transform(ftpPayloadTermLists.getTermList("value"), Transform.objToInt);
//        List<Date> dates = Lists.transform(ftpAuditTermLists.getTermList(MiraEvent.TIME), Transform.objToDate);
//        _logger.log(Level.DEBUG,name+" has "+ftpValue.size() + " results");
//        return Grapher.milliTimeSeries(name, dates, ftpValue);
//    }
//
//
//}

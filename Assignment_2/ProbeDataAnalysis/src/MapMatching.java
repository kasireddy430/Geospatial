import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MapMatching {

	static ArrayList<Probe> lstProbe = new ArrayList<Probe>();
	static ArrayList<Link> lstLink = new ArrayList<Link>();

	public static void main(String[] args) throws IOException {
		/*FileOperation fo = new FileOperation();
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Probe Points file path:-");
		String probeFilePath = scan.next();
		lstProbe = fo.readProbeData(probeFilePath);
		System.out.println("Enter Link Data file path:-");
		String linkFilePath = scan.next();
		lstLink = fo.readLinkData(linkFilePath);
		scan.close();*/


		FileOperation fo = new FileOperation();
		String probeFilePath = "C:\\Users\\Nikhil\\Desktop\\gre\\illinois\\CS513\\Assignment2\\Partition6467ProbePoints.csv";
		lstProbe = fo.readProbeData(probeFilePath);
		String linkFilePath = "C:\\Users\\Nikhil\\Desktop\\gre\\illinois\\CS513\\Assignment2\\Partition6467LinkData.csv";
		lstLink = fo.readLinkData(linkFilePath);

		Double minDistanceForLink = 0.0, minDistanceForProbe = 0.0, distanceToMapMatched = 0.0,
				previousdistanceToMapMatched = 0.0, perpendicularDistance = 0.0;
		int minIndex = 0, previousProbeID = 0;
		String closestLink = "", previousClosestLink = "";
		for (int probeIndex = 0; probeIndex < lstProbe.size(); probeIndex++) {
			Probe probe = lstProbe.get(probeIndex);

			Double[] mapMatchedPoint = null;
			for (int i = 0; i < lstLink.size(); i++) {
				Link link = lstLink.get(i);
				List<Double> distMapMatchedPoints = new ArrayList<Double>();
				for (int j = 0; j < link.getShapeInfo().size() - 1; j++) {
					Double dStart[] = link.getShapeInfo().get(j);
					Double dEnd[] = link.getShapeInfo().get(j + 1);
					Double[] lineVector = new Double[] { dEnd[0] - dStart[0], dEnd[1] - dStart[1] };
					Double[] pointVector = new Double[] { probe.getLatitude() - dStart[0],
							probe.getLongitude() - dStart[1] };
					Double magnitude = Math.sqrt(Math.pow(lineVector[0], 2) + Math.pow(lineVector[1], 2));
					Double[] lineUnitVector = new Double[] { lineVector[0] / magnitude, lineVector[1] / magnitude };
					Double[] pointUnitVector = new Double[] { pointVector[0] / magnitude, pointVector[1] / magnitude };
					Double dotProduct = lineUnitVector[0] * pointUnitVector[0] + lineUnitVector[1] * pointUnitVector[1];
					if (dotProduct < 0.0) {
						dotProduct = 0.0;
					} else if (dotProduct > 1.0) {
						dotProduct = 1.0;
					}
					mapMatchedPoint = new Double[] { lineVector[0] * dotProduct, lineVector[1] * dotProduct };
					perpendicularDistance = Math.sqrt(Math.pow(pointVector[0] - mapMatchedPoint[0], 2)
							+ Math.pow(pointVector[1] - mapMatchedPoint[1], 2)) * 1000;
					mapMatchedPoint[0] = mapMatchedPoint[0] + dStart[0];
					mapMatchedPoint[1] = mapMatchedPoint[1] + dStart[1];
					distMapMatchedPoints.add(perpendicularDistance);
				}
				minDistanceForLink = Collections.min(distMapMatchedPoints);
				if (i == 0 || minDistanceForLink < minDistanceForProbe) {
					minDistanceForProbe = minDistanceForLink;
					closestLink = link.getLinkPVID();
					minIndex = distMapMatchedPoints.indexOf(minDistanceForLink);
					distanceToMapMatched = calculateDistanceToMapMatched(link, mapMatchedPoint, minIndex);
				}
			}
			if (minDistanceForProbe < 20) {
				if (probeIndex == 0) {
					probe.setProbeDirection('U');
				} else {
					if (probe.getSampleID() == previousProbeID && previousClosestLink == closestLink) {
						if (previousdistanceToMapMatched > distanceToMapMatched) {
							probe.setProbeDirection('T');
						} else if (previousdistanceToMapMatched < distanceToMapMatched) {
							probe.setProbeDirection('F');
						} else {
							probe.setProbeDirection('U');
						}
					} else {
						probe.setProbeDirection('U');
					}
				}
				previousClosestLink = closestLink;
				previousdistanceToMapMatched = distanceToMapMatched;
				previousProbeID = probe.getSampleID();
				probe.setDistFromLink(minDistanceForProbe);
				probe.setDistFromRef(distanceToMapMatched);
				probe.setLinkPVID(closestLink);
				System.out.println("Probe no: " + probeIndex + "   ProbeSampleID: " + probe.getSampleID()
						+ "   Nearest Link: " + probe.getLinkPVID() + "   Direction of Travel: " + probe.getProbeDirection()
						+ "   Distance from Reference: " + probe.getDistFromRef() + "   Distance from Link: "
						+ probe.getDistFromLink());
			} else {
				continue;
			}
		}
		SlopeCalculate.calcuateSlope(lstLink, lstProbe);
	}

	public static double calculateDistanceToMapMatched(Link lnk, Double[] mapMatchedPoint, int index) {
		double dist = 0.0;
		for (int i = 0; i < index - 1; i++) {
			dist += haversineFormula(lnk.getShapeInfo().get(i)[0], lnk.getShapeInfo().get(i)[1],
					lnk.getShapeInfo().get(i + 1)[0], lnk.getShapeInfo().get(i + 1)[1]);
		}
		dist += haversineFormula(lnk.getShapeInfo().get(index)[0], lnk.getShapeInfo().get(index)[1], mapMatchedPoint[0],
				mapMatchedPoint[1]);
		return dist * 1000;
	}

	public static double haversineFormula(double lat1, double long1, double lat2, double long2) {
		final double R = 6372.8;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLong = Math.toRadians(long2 - long1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLong / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}
}

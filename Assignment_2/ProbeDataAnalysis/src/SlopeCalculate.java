import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlopeCalculate {

	public static void calcuateSlope(List<Link> listLink, List<Probe> matchedPoints) throws IOException {
		Probe previousProbe = null;
		for (int i = 0; i < matchedPoints.size(); i++) {
			Probe nextProbe = matchedPoints.get(i);
			if (previousProbe == null) {
				nextProbe.setSlope("U");
			} else if (nextProbe.getLinkPVID() != previousProbe.getLinkPVID()) {
				nextProbe.setSlope("U");
			} else {
				double diffAltitude = nextProbe.getAltitude() - previousProbe.getAltitude();
				double base = MapMatching.haversineFormula(nextProbe.getLatitude(), nextProbe.getLongitude(),
						previousProbe.getLatitude(), previousProbe.getLongitude());
				double slope = Math.toRadians(Math.atan(diffAltitude * 1000 / base));
				nextProbe.setSlope(String.valueOf(slope));
			}
			previousProbe = nextProbe;
			FileOperation.writeMatchedPointsFile(nextProbe, i);
			ProbetoLink(listLink, nextProbe);
		}
		System.out.println("Partition6467MatchedPoints.csv file created.");
		calculateNewSlopeMean(listLink);
	}

	public static void ProbetoLink(List<Link> listLink, Probe p) {
		for (int i = 0; i < listLink.size(); i++) {
			if (listLink.get(i).getLinkPVID() == p.getLinkPVID() && listLink.get(i).getSlopeInfo() != null) {
				ArrayList<Probe> prbList = null;
				if (listLink.get(i).getLinkProbes() != null) {
					prbList = listLink.get(i).getLinkProbes();
				} else {
					prbList = new ArrayList<>();
				}
				prbList.add(p);
				listLink.get(i).setLinkProbes(prbList);
			}
		}
	}

	public static void calculateNewSlopeMean(List<Link> listLink) throws IOException {
		int dataCount = 0;
		for (int i = 0; i < listLink.size(); i++) {

			ArrayList<Probe> prbList = listLink.get(i).getLinkProbes();
			double meanSlope = 0.0, totalSlope = 0.0, linkmeanSlope = 0.0, linkTotalSlope = 0.0;
			int count = 0, linkCount = 0;
			if (prbList != null) {
				for (Probe p : prbList) {
					String slope = p.getSlope();

					if (slope != "0" && slope != "U") {
						if (p.getProbeDirection() == 'T') {
							totalSlope -= Double.parseDouble(slope);
							count += 1;
						} else {
							totalSlope += Double.parseDouble(slope);
							count += 1;
						}
					}
				}
				if (count != 0) {
					meanSlope = totalSlope / count;
				}
				ArrayList<Double[]> linkSlope = listLink.get(i).getSlopeInfo();
				for (int k = 0; k < linkSlope.size(); k++) {
					linkTotalSlope += linkSlope.get(k)[1];
					linkCount += 1;
				}
				if (linkCount != 0) {
					linkmeanSlope = linkTotalSlope / linkCount;
				}
				FileOperation.writeSlopeData(listLink.get(i).getLinkPVID(), meanSlope, linkmeanSlope, dataCount);
				dataCount += 1;
			}
		}
		System.out.println("SlopeEvaluation.csv file created.");
	}
}

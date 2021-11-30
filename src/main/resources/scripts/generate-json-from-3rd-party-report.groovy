package scripts

import groovy.json.JsonGenerator
import groovy.json.JsonOutput

import java.util.stream.Collectors

// NOTE: The variables txt3rdPartyDepsReportPath and json3rdPartyDepsReportPath are given as argument from pom.xml

def licenses = new File(txt3rdPartyDepsReportPath).readLines().stream()
        .filter(line -> line && !line.startsWith('Lists of'))
        .map(line -> getMatches(line))
        .collect(Collectors.toUnmodifiableList());

def generator = new JsonGenerator.Options()
        .excludeNulls()
        .build()

new File(json3rdPartyDepsReportPath) << JsonOutput.prettyPrint(generator.toJson(licenses))


def getMatches(String line) {
    def matcher = line =~ /^\s+\((.*?)\)\s(?:(?=\()\((.*)\)|)(.*)\s\((.*):(.*):(.*)\s-\s(.*)\)$/
    if (matcher.matches()) {
        return [license1: matcher[0][1], license2: matcher[0][2], name: matcher[0][3],
                groupId: matcher[0][4], artifactId: matcher[0][5], version: matcher[0][6],
                url: matcher[0][7]]
    }

    throw new IllegalStateException("Regex could not match line[$line]")
}

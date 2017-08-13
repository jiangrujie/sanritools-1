package com.sanri.app.filefetch;

import java.io.File;
import java.util.Map;

public interface FilePathFilter {
	Map<String,File> mappingPkgSourcePath(String handlePath) throws IllegalArgumentException;
}

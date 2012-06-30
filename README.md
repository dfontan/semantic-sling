# Semantic Sling

[CITYTECH, Inc.](http://www.citytechinc.com)

## Requirements

* Apache Sling running on the default port
* Maven 2.x+
* [cURL](http://curl.haxx.se/) for automated deployment (optional)

## Installation

Build and Install the OSGI bundle

a. If you want to deploy the bundle automatically:

    `mvn -PautoInstallBundle clean install`

b. If you only want to build the bundle:

    `mvn clean install`

If you have content in the repository tied to the Sling instance you are installing to, you can test the install by requesting any of your content nodes with a .ttl extension.  If you have not loaded content, you can create test content following the cURL example provided in [Discovering Sling in 15 minutes](http://sling.apache.org/site/discover-sling-in-15-minutes.html).

Additional information about the project can be found at the CITYTECH, Inc. blog.

Please contact [Paul Michelotti](mailto:pmichelotti@citytechinc.com) with any questions.

## License

Copyright 2012, CITYTECH, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

#H1 WebSphere MQ CLI Rundeck Jobs
#H2 Overview
These rundeck jobs were used to install the developer version of IBM WebSphere MQ on Fedora. These jobs do not take advantage of the packaging that allows you to have multiple versions installed on a single linux host. This is a minimalist installation that takes the steps that allow you to connect to MQ with the Eclipse MQ explorer.

This exercise was to explore some of Rundecks features and capabilities. These are NOT the steps needed to configure either Rundeck or Websphere MQ environment for production usage. The fewest steps needed to get MQ ready to support a stand alone development effort. These jobs should be updated to expose more of the options availabe on the commands as parameters to the job.

#H3 Rundeck MQ Jobs
Instead of calling these jobs it might be more accurate just to call these examples of the commands one could use to set up MQ on Fedora. Most of these commands were pulled from the IBM WebSphere MQ documentation. The "jobs" have a description below is a list of the tasks they are intended to accomplish.

* Transfer staged files to Fedora
* Install MQ
* Create Queue Manager
* Update Queue Manager configuration allowing MQ explorer to connect
* Configure systemd settings to start and stop queue managers
* Commands for firewall settings

You can upload/import these XML job definitions into your rundeck project. These jobs were created/exported from version 3.0.13.You will have to define your nodes/resources and other rundeck configuration tasks to utilize these jobs.

#H3 Links

WebSphere MQ - <https://developer.ibm.com/messaging/mq-downloads/>
Rundeck - <https://www.rundeck.com/open-source/download>
MQ systemd service - <https://www.ibm.com/developerworks/community/blogs/messaging/entry/mq_linux_systemd?lang=en>




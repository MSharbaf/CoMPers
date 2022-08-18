# CoMPers
CoMPers is an extensible framework to provide proper support for configurable conflict management in personalized collaborative modeling. 
The CoMPers framework consists of a centralized [Collaboration Server](https://github.com/MSharbaf/CoMPers/tree/main/CollaborationServer) as well as several Clients that are connected to the Server. 
In our prototype implementation, we extended the Ecore-GLSP editor, which is part of the [EMF.cloud](https://www.eclipse.org/emfcloud/) project, and integrated into the Eclipse Theia IDE to provide a web-based version of the popular Ecore tools. 

![CoMPers Collaborative Modeling Archichecture](images/MainArch.png)

## Collaboration Server
The CoMPers collaboration server is central component to support collaborative modeling. It is responsible with processing change operations and conflict management. The collaboration server is located in the `CollaborationServer/` folder and its specific documentation can be found in the [Collaboration Server README](CollaborationServer/README.md).

To start working with the CoMPers collaboration server, please see the video that presents the [Welcome Page of Collaboration Server](https://drive.google.com/file/d/1mSWfnrGpHEo_jUz4iX91N5FQ-JWXG2NF/view?usp=sharing).

![Welcome Page Example](images/WelcomePage.gif)


## Ecore-GLSP Editor
An **example** of how to build the client instance for connecting to the CoMPers collaboration server is the extended version of EMF.cloud Ecore-GLSP Editor located in the `Extended-Ecore-GLSP/` folder and its specific documentation can be found in the [Extended Ecore-GLSP README](Extended-Ecore-GLSP/README.md). 
In the following, we show some possible collaboration scenarios based on the personalized change propagation approach proposed by the CoMPers framework.   

### Online-OnDemand Collaboration

For more details, please see the [Online-OnDemand Video](https://drive.google.com/file/d/1xpe0GgBEOx4pUjeULyyKV8UiwlU-V-YK/view?usp=sharing).

![Online-OnDemand Example](images/OnlineOnDemand.gif)


### Offline-OnDemand Collaboration

For more details, please see the [Offline-OnDemand Video](https://drive.google.com/file/d/1Qj2VjCZ8BoEcf9oGrMTcAwOhn_Gvg_B-/view?usp=sharing).

![Offline-OnDemand Example](images/OfflineOnDemand.gif)


### OnClose Check-out Example

For more details, please see the [Online-OnClose Video](https://drive.google.com/file/d/1mSWfnrGpHEo_jUz4iX91N5FQ-JWXG2NF/view?usp=sharing).

![Online-OnClose Example](images/PublishOnline-SubscribeOnClose.gif)


### Inconsistency Preservation Example

For more details, please see the [Online-OnClose Video](https://drive.google.com/file/d/1dui2h7AHNNv6RkV-2-vvvTvon150o0gc/view?usp=sharing).

![Inconsistency Preservation Example](images/InconsistencyPreservation.gif)


### Conflict Handling Example

For more details, please see the [Online-OnClose Video](https://drive.google.com/file/d/1mSWfnrGpHEo_jUz4iX91N5FQ-JWXG2NF/view?usp=sharing).

![Conflict Handling Example](images/ConflictHandling.gif)


## 27 March 2014: We have moved to GitHub ##

# [OmniFaces is now at GitHub behind http://omnifaces.org!](http://omnifaces.org) #

Any future commits will end up there. We will not commit to Google Code anymore. Please update your bookmarks to http://omnifaces.org. Please update the link from where you ended up here to point to http://omnifaces.org instead.

Main reasons:

  * Google code stopped offering its download service. Initially only for new projects, but then for existing projects as well. This is not really a big problem as we can just provide Maven links, but all in all it start to look like that Google Code is on the "kill" list of Google Products and will eventually completely disappear.

  * We use Git at our recent jobs. Mercurial had some shortcomings, among others problems with moving directories(+) and offering the right snapshot version for the builds, causing the builds to not be reproducible. Hence we moved from Mercurial to Git. Of course Google Code supports Git just as well, but since we had to rebuild the repo anyway we took this as an opportunity to move to Github.

You _can_ keep reporting issues here, but it's **strongly recommend** to do it [over there at GitHub](https://github.com/omnifaces/omnifaces/issues). In the upcoming weeks/months we will wade through the currently open issues at Google Code and fix/close/move them where applicable.

(+) Technically git suffers from the same problem, but when merging it uses heuristics that typically end up being the right thing.

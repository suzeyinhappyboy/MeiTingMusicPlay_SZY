# MeiTingMusicPlay_SZY
扫描本地或者获取服务器端的音乐文件，将信息存储到数据库并添加到播放列表播放；歌词滚动显示等功能。                                    
UI界面简介：
----
主页面由自定义ActionBar和Fragment嵌套组成。
自定义ActionBar由ViewPager嵌套Fragment组成
主Fragment由ViewPager、自定义View（包括GridView ListView等）等构成
播放列表有两个，一个是本地播放列表，一个是获取服务器歌曲的在线播放列表
播放界面有进度条，滚动歌词等常见音乐播放器应具备的功能

##功能知识点

扫描歌曲是播放器的基本功能，一般通过ContentProvider配合Media相关类查询系统数据库，获得媒体库中的歌曲信息。
弊端是依赖于Android系统媒体库，有时新增音乐后没有通知系统扫描，就无法获得该音乐的信息，不够灵活。
本项目采用搜索MP3，wma等格式文件，有音乐文件更新可以手搜索添加
可以直接下载APK体验

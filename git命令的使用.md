## 快速参考

```java

// 本地建立git仓库
git init

// 从github下载代码
git clone git@github.com:xxx/xxx.git

// 本地代码关联github库:
git remote add origin https://github.com/xxx/xxx

// pull代码到本地
git pull origin master

// 添加一个名为host的分支
git checkout -b host xxxxx

// 切换到主干
git checkout master 

// 合并分支host到主干
git merge host 
git commit -m “描述”

 // 把本地源码库push到Github上,在master分支可以这么写
git push origin master
// 如果是其他自建分支,需要指定主分支master提交
git push origin HEAD:master
// 或者 git push origin HEAD:refs/for/master

//git reset --hard HEAD 后,丢失找回
git reflog
    b7057a9 HEAD@{0}: reset: moving to b7057a9
    98abc5a HEAD@{1}: commit: more stuff added to foo
    b7057a9 HEAD@{2}: commit (initial): initial commit
//如找回第二commit，只需要做如下操作：
$ git reset --hard 98abc5a

```

## git规范提交:

```
git支持很多种工作流程，我们采用的一般是这样，远程创建一个主分支，本地每人创建功能分支，日常工作流程如下：
去自己的工作分支
$ git checkout work   //工作
....
提交工作分支的修改
$ git commit -a
回到主分支
$ git checkout master
获取远程最新的修改，此时不会产生冲突
$ git pull
回到工作分支
$ git checkout work
用rebase合并主干的修改，如果有冲突在此时解决
$ git rebase master
回到主分支
$ git checkout master
合并工作分支的修改，此时不会产生冲突。
$ git merge work
提交到远程主干
$ git push
这样做的好处是，远程主干上的历史永远是线性的。每个人在本地分支解决冲突，不会在主干上产生冲突。
```

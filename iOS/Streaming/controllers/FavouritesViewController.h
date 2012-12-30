//
//  FavouritesViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FavouritesViewController : UIViewController<UITableViewDelegate, UITableViewDataSource>
{
	IBOutlet UITableView *favouritesTable;
	
	NSArray *websites;
}

@property (nonatomic, strong) NSArray *websites;

@end

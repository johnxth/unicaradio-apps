//
//  FavouritesViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "FavouritesViewController.h"

#import "SystemUtils.h"

@interface FavouritesViewController ()

@end

@implementation FavouritesViewController

@synthesize websites;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_FAVOURITES", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"favorites"];

		NSString *plistPath = [[NSBundle mainBundle] pathForResource:@"websites" ofType:@"plist"];
		self.websites = [NSArray arrayWithContentsOfFile:plistPath];
		NSLog(@"Size: %d", [self.websites count]);

		if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
			self.edgesForExtendedLayout = UIRectEdgeNone;
			self.extendedLayoutIncludesOpaqueBars = NO;
			self.automaticallyAdjustsScrollViewInsets = NO;
		}

		[self initButtonBarItems];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

	favouritesTable.rowHeight = 55;
	favouritesTable.backgroundColor = [UIColor blackColor];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
	return 1;
}

// Setta il numero di righe della tabella .
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [self.websites count];
}

// Setta il contenuto delle varie celle
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell;
	cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
	cell.selectionStyle = UITableViewCellSelectionStyleNone;
	
	UIColor *textColor = [UIColor whiteColor];
	
	NSInteger index = indexPath.row;
	cell.textLabel.text = [[self.websites objectAtIndex:index] objectForKey:@"description"];
	cell.detailTextLabel.text = [[self.websites objectAtIndex:index] objectForKey:@"address"];

	NSString *imageName = [[self.websites objectAtIndex:index] objectForKey:@"icon_name"];

	UIImage *cellImage = [UIImage imageNamed:imageName];
	cell.imageView.image = cellImage;

	cell.backgroundColor = [UIColor clearColor];
	[cell.textLabel setTextColor:textColor];
	[cell.detailTextLabel setTextColor:textColor];
	
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSLog(@"selected row: %d", indexPath.row);
	NSString *urlString = [[self.websites objectAtIndex:indexPath.row] objectForKey:@"address"];
	NSLog(@"url: %@", urlString);
	[[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlString ]];
}

@end
